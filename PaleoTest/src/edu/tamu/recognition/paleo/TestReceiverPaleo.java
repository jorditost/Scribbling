package edu.tamu.recognition.paleo;
/* $Id$
 * Created on 28.10.2003
 */

/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Auracle
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 */
//import java.awt.*;
//import java.awt.event.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
//import java.util.Date;

//import javax.swing.*;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
//import com.illposed.osc.ui.OscUI;
import com.illposed.osc.OSCPortOut;


import java.awt.AWTException;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.LinkedList;


//
//import com.sun.speech.freetts.Voice;
//import com.sun.speech.freetts.VoiceManager;

public class TestReceiverPaleo {
	

	public TestReceiverPaleo() {
			
		
		OSCPortIn receiver = null;
		try {
			receiver = new OSCPortIn(8000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		OscMessageListenerPaleo listener = new OscMessageListenerPaleo();
		listener.instantiatePaleo();
		receiver.addListener("/raw", listener);
		
//		/messages OSC               
//		/raw 100 200 200 300 (cyclops -> paleo) 									| 8000 | localhost
//		/clean 100 200 200 300 (paleo -> cyclops )									| 7000 | localhost
//		/cut operator(red|black|image) 100 200 200 300 (cyclops  -> laserserver )	| 9000 | lasercutterPC
		
		
		receiver.startListening();
		
	}

	public static void main(String args[]) {
		new TestReceiverPaleo();
	
	}
}



