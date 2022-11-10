package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AtlasScaler extends ApplicationAdapter {
    public Array<String> files;

    public AtlasScaler(String[] filenames) {
        files = new Array<>(filenames.length);
        for (int i = 0; i < filenames.length; i++) {
            if(filenames[i].endsWith(".atlas"))
                files.add(filenames[i]);
        }
    }

    public FileHandle load(String name) {
        FileHandle fh = Gdx.files.local(name);
        if(fh.exists()) return fh;
        fh = Gdx.files.absolute(name);
        if(fh.exists()) return fh;
        return null;
    }

    @Override
    public void create() {
/* ;; original Clojure code, meant for a REPL
(doseq [n [2 3 4]]
 (spit (str "Dawnlike" n ".atlas")
       (-> text
           (clojure.string/replace "Dawnlike.png"
                                   (str "Dawnlike" n ".png"))
           (clojure.string/replace #"(\d+),(?: ?)(\d+)"
                                   (fn [[_ l r]]
                                       (str
                                         (* (Integer/parseInt l) n)
                                         ","
                                         (* (Integer/parseInt r) n)))))))
 */
        final Pattern pairs = Pattern.compile("(\\d+), ?(\\d+)");
        for(String name : files) {
            FileHandle original = load(name);
            if(original == null) continue;
            String text = original.readString("UTF8");
            TextureAtlas atlas = new TextureAtlas(original);
            ObjectSet<Texture> textures = atlas.getTextures();
            for (int n : new int[]{2, 3, 4, 6, 8}) {
                String outputName = (original.pathWithoutExtension() + "-x" + n + ".atlas");
                FileHandle output;
                String working = text;
                if(original.type() == Files.FileType.Local)
                    output = Gdx.files.local(outputName);
                else
                    output = Gdx.files.absolute(outputName);
                for(Texture tex : textures){
                    FileHandle th = ((FileTextureData)tex.getTextureData()).getFileHandle();
                    working = working.replaceAll(Pattern.quote(th.name()), th.nameWithoutExtension() + "-x" + n + "." + th.extension());
                }
                Matcher matcher = pairs.matcher(working);
                StringBuffer buf = new StringBuffer(working.length());
                while (matcher.find()){
                    int left = Integer.parseInt(matcher.group(1)) * n, right = Integer.parseInt(matcher.group(2)) * n;
                    matcher.appendReplacement(buf, left + "," + right);
                }
                matcher.appendTail(buf);
                output.writeString(buf.toString(), false, "UTF8");
            }
        }
        Gdx.app.exit();
    }
}
