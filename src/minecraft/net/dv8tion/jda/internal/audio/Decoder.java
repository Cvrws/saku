/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dv8tion.jda.internal.audio;

import com.sun.jna.ptr.PointerByReference;
import net.dv8tion.jda.api.audio.OpusPacket;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Class that wraps functionality around the Opus decoder.
 */
public class Decoder
{
    protected int ssrc;
    protected char lastSeq;
    protected int lastTimestamp;
    protected PointerByReference opusDecoder;

    protected Decoder(int ssrc)
    {
        this.ssrc = ssrc;
        this.lastSeq = (char) -1;
        this.lastTimestamp = -1;

        IntBuffer error = IntBuffer.allocate(1);

    }

    public boolean isInOrder(char newSeq)
    {
        return lastSeq == (char) -1 || newSeq > lastSeq || lastSeq - newSeq > 10;
    }

    public boolean wasPacketLost(char newSeq)
    {
        return newSeq > lastSeq + 1;
    }

    public short[] decodeFromOpus(AudioPacket decryptedPacket)
    {
        int result;
        ShortBuffer decoded = ShortBuffer.allocate(4096);
        if (decryptedPacket == null)    //Flag for packet-loss
        {
            result = -1;
            lastSeq = (char) -1;
            lastTimestamp = -1;
        }
        else
        {
            this.lastSeq = decryptedPacket.getSequence();
            this.lastTimestamp = decryptedPacket.getTimestamp();

            ByteBuffer encodedAudio = decryptedPacket.getEncodedAudio();
            int length = encodedAudio.remaining();
            int offset = encodedAudio.arrayOffset() + encodedAudio.position();
            byte[] buf = new byte[length];
            byte[] data = encodedAudio.array();
            System.arraycopy(data, offset, buf, 0, length);
            result = -1;
        }

        //If we get a result that is less than 0, then there was an error. Return null as a signifier.
        if (result < 0)
        {
            handleDecodeError(result);
            return null;
        }

        short[] audio = new short[result * 2];
        decoded.get(audio);
        return audio;
    }

    private void handleDecodeError(int result)
    {
        StringBuilder b = new StringBuilder("Decoder failed to decode audio from user with code ");
        AudioConnection.LOG.debug("{}", b);
    }

    protected synchronized void close()
    {
        if (opusDecoder != null)
        {
            opusDecoder = null;
        }
    }

    @Override
    @SuppressWarnings("deprecation") /* If this was in JDK9 we would be using java.lang.ref.Cleaner instead! */
    protected void finalize() throws Throwable
    {
        super.finalize();
        close();
    }
}
