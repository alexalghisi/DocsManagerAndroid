package com.example.ilazar.mykeep.net.mapping;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourceListReader<E> implements ResourceReader<List<E>, JsonReader> {

    private final ResourceReader<E, JsonReader> mResourceReader;

    public ResourceListReader(ResourceReader<E, JsonReader> resourceReader) {
        mResourceReader = resourceReader;
    }

    @Override
    public List<E> read(JsonReader reader) throws Exception {
        List<E> entityList = new ArrayList<E>();

        reader.beginObject();

        while(reader.hasNext()) {
            if(reader.hasNext()) {
                String name = reader.nextName();
                Log.d("Reader->", name);
                if(name.equals("page")) {
                    int pageID = reader.nextInt();
                    Log.d("Page:", Integer.toString(pageID));
                } else if (name.equals("notes")) {
                    reader.beginArray();
                    while(reader.hasNext())
                        entityList.add(mResourceReader.read(reader));
                    reader.endArray();
                } else if(name.equals("more")) {
                    boolean more = reader.nextBoolean();
                }
            }
        }
        reader.endObject();
        /*
        reader.beginArray();

        while (reader.hasNext()) {
            entityList.add(mResourceReader.read(reader));
        }
        reader.endArray();*/
        return entityList;
    }
}
