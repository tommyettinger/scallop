package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.Scallop;

/** Launches the desktop (headless) application. */
public class ScallopLauncher {
	public static void main(String[] args) { 
		new HeadlessApplication(new Scallop(args), getDefaultConfiguration());
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		return configuration;
	}
}
