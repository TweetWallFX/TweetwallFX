package org.tweetwallfx.controls.dataprovider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CachedMedia implements Externalizable {
    private static final long serialVersionUID = 1L;
    private byte[] data;

    public CachedMedia(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        while ((read = in.read(buffer)) > -1) {
            bout.write(buffer, 0, read);
        }
        data = bout.toByteArray();
    }

    public CachedMedia() {
        data = new byte[0];
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        data = new byte[size];
        int read = in.read(data);
        if (read != size) {
            throw new IOException(
                    "Unexpected amount of data read. Expected " + size + ", got " + read);
        }
    }
}
