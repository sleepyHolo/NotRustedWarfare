package nrw.helpers.masters;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public  class GsonLoader<T> extends AsynchronousAssetLoader<T, AssetLoaderParameters<T>> {

    private final Gson gson;
    private T strings;
    private final Type type;

    public GsonLoader(FileHandleResolver resolver, Type type) {
        super(resolver);
        gson = new Gson();
        this.type = type;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String s, FileHandle file, AssetLoaderParameters<T> parameters) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String filename, FileHandle file, AssetLoaderParameters<T> parameters) {
        strings = gson.fromJson(file.reader(String.valueOf(StandardCharsets.UTF_8)), type);
    }

    @Override
    public T loadSync(AssetManager manager, String filename, FileHandle file, AssetLoaderParameters<T> parameters) {
        return strings;
    }
}
