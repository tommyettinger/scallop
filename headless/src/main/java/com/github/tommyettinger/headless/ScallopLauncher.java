package com.github.tommyettinger.headless;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.github.tommyettinger.AtlasScaler;
import com.github.tommyettinger.Scallop;

import java.util.concurrent.atomic.AtomicInteger;

/** Launches the desktop (headless) application. */
public class ScallopLauncher {
	public static void main(String[] args) {
		final AtomicInteger runCount = new AtomicInteger(0);
		final HeadlessApplicationConfiguration config = getDefaultConfiguration();
		new HeadlessApplication(new Scallop(args, runCount), config);
		new HeadlessApplication(new AtlasScaler(args, runCount), config);
	}

	private static HeadlessApplicationConfiguration getDefaultConfiguration() {
		return new HeadlessApplicationConfiguration();
	}
}
