package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.io.IOException;
import java.nio.ByteBuffer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Scallop extends ApplicationAdapter {
	public String[] files;

	public Scallop(String[] filenames) {
		files = filenames;
	}

	public static void scale2x(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
							   int p2, int p3) {
		dest.putInt(p0, D != 0 && ((D == B && B != F && D != H) || (E == 0 && B != 0)) ? D : E);
		dest.putInt(p1, B != 0 && ((B == F && B != D && F != H) || (E == 0 && F != 0)) ? B : E);
		dest.putInt(p2, H != 0 && ((H == D && D != B && H != F) || (E == 0 && D != 0)) ? H : E);
		dest.putInt(p3, F != 0 && ((F == H && D != H && B != F) || (E == 0 && H != 0)) ? F : E);
	}

	public static int lerp(int baseColor, int mixColor, float amount) {
		if ((baseColor & 0x80) == 0)
			return mixColor;
		if ((mixColor & 0x80) == 0)
			return baseColor;
		final float i = 1f - amount;
		final int r = (int) ((baseColor >>> 24) * i + (mixColor >>> 24) * amount), g = (int) ((baseColor >>> 16 & 0xFF) * i
				+ (mixColor >>> 16 & 0xFF) * amount), b = (int) ((baseColor >>> 8 & 0xFF) * i + (mixColor >>> 8 & 0xFF) * amount);
		return r << 24 | g << 16 | b << 8 | 0xFF;
	}

	public static void scale2k(ByteBuffer dest, int A, int B, int C, int D, int E, int F, int G, int H, int I, int p0, int p1,
							   int p2, int p3) {
		if (D == B && B != F && D != H) {
			if (B == C && D == G) {
				if (A != E) {
					dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
					dest.putInt(p1, lerp(D, dest.getInt(p1), 0.25f));
					dest.putInt(p2, lerp(D, dest.getInt(p2), 0.25f));
				}
			} else if (B == C) {
				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
				dest.putInt(p1, lerp(D, dest.getInt(p1), 0.25f));
			} else if (D == G) {
				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.75f));
				dest.putInt(p2, lerp(D, dest.getInt(p2), 0.25f));
			} else {
				dest.putInt(p0, lerp(D, dest.getInt(p0), 0.5f));
			}
		}
	}

	public static void scaleX(Pixmap src, Pixmap dest) {

		final int width = src.getWidth() - 1, height = src.getHeight() - 1, dw = dest.getWidth(), dh = dest.getHeight();
		ByteBuffer pixels = dest.getPixels();

		for (int y = 0; y <= height; ++y) {
			for (int x = 0; x <= width; ++x) {
				int p0, p1, p2, p3;
				int A, B, C, D, E, F, G, H, I;

				A = (x & y) == 0 ? 0 : src.getPixel(x - 1, y - 1);
				B = y == 0 ? 0 : src.getPixel(x, y - 1);
				C = y == 0 || x == width ? 0 : src.getPixel(x + 1, y - 1);
				D = x == 0 ? 0 : src.getPixel(x - 1, y);
				E = src.getPixel(x, y);
				F = x == width ? 0 : src.getPixel(x + 1, y);
				G = x == 0 || y == height ? 0 : src.getPixel(x - 1, y + 1);
				H = y == height ? 0 : src.getPixel(x, y + 1);
				I = x == width || y == height ? 0 : src.getPixel(x + 1, y + 1);

				p0 = (y * dw + x << 1) << 2;
				p1 = (y * dw + x << 1 | 1) << 2;
				p2 = ((y * dw + x << 1) + dw) << 2;
				p3 = ((y * dw + x << 1 | 1) + dw) << 2;

				scale2x(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
			}
		}

		for (int y = 1; y < dh; y ++) {
			for (int x = 1; x < dw; x ++) {
				int p0, p1, p2, p3, c0, c1, c2, c3;
				c0 = pixels.getInt(p0 = (y * dw + x - 1 - dw) << 2);
				c1 = pixels.getInt(p1 = (y * dw + x - dw) << 2);
				c2 = pixels.getInt(p2 = ((y * dw + x - 1)) << 2);
				c3 = pixels.getInt(p3 = ((y * dw + x)) << 2);
				if (c0 == c3 && c1 == c2 && c0 != c1)
				{
					c3 = (Integer.bitCount(c0) > Integer.bitCount(c1) ? c0 : c1);
					pixels.putInt(p0, c3);
					pixels.putInt(p1, c3);
					pixels.putInt(p2, c3);
					pixels.putInt(p3, c3);
				}
				else if (c0 == c3 && c0 != c1 && c0 != c2)
				{
					pixels.putInt(p1, c0);
					pixels.putInt(p2, c0);
				}
				else if (c1 == c2 && c1 != c0 && c1 != c3)
				{
					pixels.putInt(p0, c1);
					pixels.putInt(p3, c1);
				}
			}
		}
	}

	public static void scaleK(Pixmap src, Pixmap dest) {

		final int width = src.getWidth() - 1, height = src.getHeight() - 1, dw = dest.getWidth(), dh = dest.getHeight();
		ByteBuffer pixels = dest.getPixels();

		for (int y = 1; y < height; ++y) {
			for (int x = 1; x < width; ++x) {
				int p0, p1, p2, p3;
				int A, B, C, D, E, F, G, H, I;

				A = src.getPixel(x - 1, y - 1);
				B = src.getPixel(x, y - 1);
				C = src.getPixel(x + 1, y - 1);
				D = src.getPixel(x - 1, y);
				E = src.getPixel(x, y);
				F = src.getPixel(x + 1, y);
				G = src.getPixel(x - 1, y + 1);
				H = src.getPixel(x, y + 1);
				I = src.getPixel(x + 1, y + 1);

				p0 = (y * dw + x << 1) << 2;
				p1 = (y * dw + x << 1 | 1) << 2;
				p2 = ((y * dw + x << 1) + dw) << 2;
				p3 = ((y * dw + x << 1 | 1) + dw) << 2;

				scale2x(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
				
				scale2k(pixels, A, B, C, D, E, F, G, H, I, p0, p1, p2, p3);
				scale2k(pixels, G, D, A, H, E, B, I, F, C, p2, p0, p3, p1);
				scale2k(pixels, I, H, G, F, E, D, C, B, A, p3, p2, p1, p0);
				scale2k(pixels, C, F, I, B, E, H, A, D, G, p1, p3, p0, p2);
			}
		}
	}

	public void create() {
		PixmapIO.PNG png = new PixmapIO.PNG();
		png.setFlipY(false);
		for (String s : files) {
			boolean isAbsolute = s.matches(".*[/\\\\].*");
			FileHandle fh = isAbsolute ? Gdx.files.absolute(s) : Gdx.files.local(s);
			Pixmap source = new Pixmap(fh);
			Pixmap dest = new Pixmap(source.getWidth() * 2, source.getHeight() * 2, Pixmap.Format.RGBA8888);
			Pixmap dest4 = new Pixmap(source.getWidth() * 4, source.getHeight() * 4, Pixmap.Format.RGBA8888);
			Pixmap dest8 = new Pixmap(source.getWidth() * 8, source.getHeight() * 8, Pixmap.Format.RGBA8888);
			scaleX(source, dest);
			scaleX(dest, dest4);
			scaleX(dest4, dest8);
			try {
				if(isAbsolute) {
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x2.png"), dest);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x4.png"), dest4);
					png.write(Gdx.files.absolute(fh.pathWithoutExtension() + "-x8.png"), dest8);
				}
				else {
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x2.png"), dest);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x4.png"), dest4);
					png.write(Gdx.files.local(fh.pathWithoutExtension() + "-x8.png"), dest8);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				source.dispose();
				dest.dispose();
				dest4.dispose();
				dest8.dispose();
			}
		}
		Gdx.app.exit();
	}
}
