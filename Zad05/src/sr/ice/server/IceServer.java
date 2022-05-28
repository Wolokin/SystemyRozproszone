package sr.ice.server;
// **********************************************************************
//
// Copyright (c) 2003-2019 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Identity;
import sr.ice.server.devices.bulbulator.BulbulatorI;

public class IceServer
{
	public void t1(String[] args)
	{
		int status = 0;
		Communicator communicator = null;

		try	{
			// 1. Inicjalizacja ICE - utworzenie communicatora
			communicator = Util.initialize(args);

			// 2. Konfiguracja adaptera
			ObjectAdapter adapter = communicator.createObjectAdapter("SmartHome1");

			// 3. Stworzenie serwanta/serwantów
			BulbulatorI bulbulatorServant = new BulbulatorI();
						    
			// 4. Dodanie wpisów do tablicy ASM, skojarzenie nazwy obiektu (Identity) z serwantem 
			adapter.add(bulbulatorServant, new Identity("Bulbulator", "IoT"));
	        
			// 5. Aktywacja adaptera i wejœcie w pêtlê przetwarzania ¿¹dañ
			adapter.activate();
			
			System.out.println("Entering event processing loop...");
			
			communicator.waitForShutdown(); 		
			
		}
		catch (Exception e) {
			System.err.println(e);
			status = 1;
		}
		if (communicator != null) {
			try {
				communicator.destroy();
			}
			catch (Exception e) {
				System.err.println(e);
				status = 1;
			}
		}
		System.exit(status);
	}


	public static void main(String[] args)
	{
		IceServer app = new IceServer();
		app.t1(args);
	}
}
