/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.applet;

import boofcv.gui.ProcessInput;
import boofcv.struct.image.ImageFloat32;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Applet for visualizations which take in as input a single image.
 *
 * @author Peter Abeles
 */
public class VideoInputApplet extends JApplet {

	@Override
	public void init() {
		showStatus("Loading Parameters");
		String dataset = getParameter("dataset");

		if( dataset == null ) {
			showStatus("dataset parameter not set!");
			return;
		}

		final String panelPath = getParameter("panelPath");
		Class inputType = ImageFloat32.class;

		showStatus("Creating GUI component");
		final JComponent comp = FactoryVisualPanel.create(panelPath,inputType);

		if( comp == null ) {
			showStatus("Failed to create GUI component");
			return;
		} else if( !(comp instanceof ProcessInput) ) {
			showStatus("The loaded component is not of ProcessImage type");
			return;
		}

		showStatus("Loading Image");
		ProcessInput p = (ProcessInput)comp;

		AppletVideoListManager manager = parseImageInput(inputType,dataset);

		p.setInputManager(manager);

		// wait for it to process one image so that the size isn't all screwed up
		while( !p.getHasProcessedImage() ) {
			Thread.yield();
		}

		showStatus("Running");
		getContentPane().add(comp, BorderLayout.CENTER);
	}

	public AppletVideoListManager parseImageInput( Class imageType , String fileName )
	{

		String directory = getDirectory(fileName);

		AppletVideoListManager ret = new AppletVideoListManager(imageType,getCodeBase());
		try {
			InputStreamReader isr = new InputStreamReader(new URL(getCodeBase(),fileName).openStream());
			BufferedReader reader = new BufferedReader(isr);

			String line;
			while( (line = reader.readLine()) != null ) {
				if( line.length() == 0 )
					continue;

				String[]z = line.split(":");
				String[] names = new String[z.length-1];
				for( int i = 1; i < z.length; i++ ) {
					names[i-1] = directory+z[i];
				}

				ret.add(z[0],null,names);
			}

			return ret;

		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Given the path to the file it extracts the directory containing the file
	 */
	public static String getDirectory( String fileName ) {
		int tailLength = 0;
		while( tailLength < fileName.length() ) {
			if( fileName.charAt(fileName.length()-tailLength-1) == '/') {
				break;
			} else {
				tailLength++;
			}
		}
		return fileName.substring(0,fileName.length()-tailLength);
	}

	@Override
	public String getAppletInfo() {
		return "Shows gradient of an image";
	}
}
