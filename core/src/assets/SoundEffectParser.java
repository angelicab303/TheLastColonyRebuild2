/*
 * SoundBufferParser.java
 *
 * This is an interface for parsing a JSON entry into a SoundBuffer asset. SoundBuffer
 * assets do not have any properties other than the file name (because all properties)
 * are stored in the file.
 *
 * @author Walker M. White
 * @data   04/20/2020
 */
package assets;

import audio.SoundEffect;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * This class parses a JSON entry into a {@link SoundEffect}.
 *
 * SoundBuffer assets do not have any properties other than the file name (because 
 * all properties) are stored in the file.
 */
public class SoundEffectParser implements AssetParser<SoundEffect> {
    /** The current font entry in the JSON directory */
    private JsonValue root;

    /**
     * Returns the asset type generated by this parser
     *
     * @return the asset type generated by this parser
     */
    public Class<SoundEffect> getType() {
        return SoundEffect.class;
    }

    /**
     * Resets the parser iterator for the given directory.
     *
     * The value directory is assumed to be the root of a larger JSON structure.
     * The individual assets are defined by subtrees in this structure.
     *
     * @param directory    The JSON representation of the asset directory
     */
    public void reset(JsonValue directory) {
        root = directory;
        root = root.getChild( "soundfx" );
    }

    /**
     * Returns true if there are still assets left to generate
     *
     * @return true if there are still assets left to generate
     */
    public boolean hasNext() {
        return root != null;
    }

    /**
     * Processes the next available sound buffer, loading it into the asset manager
     *
     * {@link SoundEffect} objects have no additional loader properties.  They are
     * specified key : filename.
     *
     * This method fails silently if there are no available assets to process.
     *
     * @param manager    The asset manager to load an asset
     * @param keymap    The mapping of JSON keys to asset file names
     */
    public void processNext(AssetManager manager, ObjectMap<String,String> keymap) {
        String file = root.asString();
        String asset = file+":"+root.name();
        keymap.put(root.name(), asset);
        manager.load( asset, SoundEffect.class, null );
        root = root.next();
    }

    /**
     * Returns true if o is another SoundBufferParser
     *
     * @return true if o is another SoundBufferParser
     */
    public boolean equals(Object o) {
        return o instanceof SoundEffectParser;
    }

}