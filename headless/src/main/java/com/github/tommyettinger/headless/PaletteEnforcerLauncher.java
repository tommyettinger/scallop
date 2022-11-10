package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.PaletteEnforcer;

/** Launches the desktop (headless) application. */
public class PaletteEnforcerLauncher {
	public static void main(String[] args) { 
		new HeadlessApplication(new PaletteEnforcer(args), getDefaultConfiguration());
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
		return configuration;
	}
}
