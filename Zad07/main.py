import time
import subprocess

from kazoo.client import KazooClient
from kazoo.protocol.states import EventType

WATCHED_ZNODE = "/z"


class App:
    def __init__(self, hosts):
        self.zk = KazooClient(hosts=hosts)
        self.zk.start()
        self.proc = None
        self.open_app = False

        @self.zk.DataWatch(WATCHED_ZNODE)
        def z_watcher(_data, _stat, event):
            if event:
                print(event)
                if event.type == EventType.CREATED:
                    self.zk.ChildrenWatch(WATCHED_ZNODE, send_event=True, func=self.children_watcher)
                    print("Please enter the process name (default: gedit)")
                    self.open_app = True
                if event.type == EventType.DELETED:
                    if self.proc:
                        self.proc.terminate()

    def children_watcher(self, children, event):
        if event and event.type == EventType.CHILD:
            print(event)
            for child in children:
                path = event.path + "/" + child
                self.zk.ChildrenWatch(path, send_event=True, func=self.children_watcher)
            print("Current descendant count:", self.count_nodes(WATCHED_ZNODE)-1)

    def count_nodes(self, path):
        return 1 + sum([self.count_nodes(path + "/" + p) for p in self.zk.get_children(path)])

    def print_tree(self, path: str, sep: str = "//"):
        if not self.zk.exists(path):
            return
        depth = path.count("/")-1
        line = "|   "*(depth-1)
        line += "|-->" if depth > 0 else ""
        line += "(" + path.split(sep)[-1] + ")"
        print(line)
        for child in self.zk.get_children(path):
            self.print_tree(path + "/" + child, sep)

    def start(self):
        default = "gedit"
        while True:
            cmd = input()
            if self.open_app:
                try:
                    self.proc = subprocess.Popen(cmd)
                except FileNotFoundError as e:
                    print(f"Couldn't find ({cmd}), defaulting to {default}")
                    self.proc = subprocess.Popen(default)
                except PermissionError as e:
                    self.proc = subprocess.Popen(default)
                self.open_app = False
            elif cmd == "#T":
                self.print_tree(WATCHED_ZNODE)
            elif cmd.startswith("#T"):
                self.print_tree(WATCHED_ZNODE, cmd[2:])


if __name__ == '__main__':
    app = App('127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183')
    app.start()
