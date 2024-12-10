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

package net.dv8tion.jda.api.managers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.internal.utils.JDALogger;


/**
 * AudioManager deals with creating, managing and severing audio connections to
 * {@link VoiceChannel VoiceChannels}. Also controls audio handlers.
 *
 * @see Guild#getAudioManager()
 */
public interface AudioManager
{
    long DEFAULT_CONNECTION_TIMEOUT = 10000;

    /**
     * Starts the process to create an audio connection with an {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel}
     * or, if an audio connection is already open, JDA will move the connection to the provided AudioChannel.
     * <br><b>Note</b>: Currently you can only be connected to a single {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel}
     * per {@link net.dv8tion.jda.api.entities.Guild Guild}.
     *
     * <p>This method will automatically move the current connection if one connection is already open in this underlying {@link Guild}.
     * <br>Current connections can be closed with {@link #closeAudioConnection()}.
     *
     * @param  channel
     *         The {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel} to open an audio connection with.
     *
     * @throws IllegalArgumentException
     *         <ul>
     *             <li>If the provided channel was {@code null}.</li>
     *             <li>If the provided channel is not part of the Guild that the current audio connection is connected to.</li>
     *         </ul>
     * @throws UnsupportedOperationException
     *         If audio is disabled due to an internal JDA error
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException
     *         <ul>
     *             <li>If the currently logged in account does not have the Permission {@link net.dv8tion.jda.api.Permission#VOICE_CONNECT VOICE_CONNECT}</li>
     *             <li>If the currently logged in account does not have the Permission {@link net.dv8tion.jda.api.Permission#VOICE_MOVE_OTHERS VOICE_MOVE_OTHERS}
     *                 and the {@link VoiceChannel#getUserLimit() user limit} has been exceeded!</li>
     *         </ul>
     */
    void openAudioConnection(AudioChannel channel);

    /**
     * Close down the current audio connection of this {@link net.dv8tion.jda.api.entities.Guild Guild}
     * and disconnects from the {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel}.
     * <br>If this is called when JDA doesn't have an audio connection, nothing happens.
     */
    void closeAudioConnection();

    /**
     * Gets the {@link net.dv8tion.jda.api.JDA JDA} instance that this AudioManager is a part of.
     *
     * @return The corresponding JDA instance
     */
    @Nonnull
    JDA getJDA();

    /**
     * Gets the {@link net.dv8tion.jda.api.entities.Guild Guild} instance that this AudioManager is used for.
     *
     * @return The Guild that this AudioManager manages.
     */
    @Nonnull
    Guild getGuild();

    /**
     * The {@link AudioChannelUnion} that JDA currently has an audio connection to.
     * <br>If JDA currently doesn't have an active audio connection, this will return {@code null}.
     *
     * @return The {@link AudioChannelUnion} the audio connection is connected to, or {@code null} if not connected.
     */
    @Nullable
    AudioChannelUnion getConnectedChannel();

    /**
     * This can be used to find out if JDA currently has an active audio connection with a
     * {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel}. If this returns true, then
     * {@link #getConnectedChannel()} will return the {@link net.dv8tion.jda.api.entities.channel.middleman.AudioChannel AudioChannel} which
     * JDA is connected to.
     *
     * @return True, if JDA currently has an active audio connection.
     */
    boolean isConnected();

    /**
     * Sets the amount of time, in milliseconds, that will be used as the timeout when waiting for the audio connection
     * to successfully connect. The default value is 10 second (10,000 milliseconds).
     * <br><b>Note</b>: If you set this value to 0, you can remove timeout functionality and JDA will wait FOREVER for the connection
     * to be established. This is no advised as it is possible that the connection may never be established.
     *
     * @param timeout
     *        The amount of time, in milliseconds, that should be waited when waiting for the audio connection
     *        to be established.
     */
    void setConnectTimeout(long timeout);

    /**
     * The currently set timeout value, in <b>milliseconds</b>, used when waiting for an audio connection to be established.
     *
     * @return The currently set timeout.
     */
    long getConnectTimeout();


    void setAutoReconnect(boolean shouldReconnect);

    /**
     * Whether audio connections from this AudioManager automatically reconnect
     *
     * @return Whether audio connections from this AudioManager automatically reconnect
     */
    boolean isAutoReconnect();

    /**
     * Set this to {@code true} if the current connection should be displayed as muted,
     * this will cause the {@link net.dv8tion.jda.api.audio.AudioSendHandler AudioSendHandler} packages
     * to not be ignored by Discord!
     *
     * @param muted
     *        Whether the connection should stop sending audio
     *        and display as muted.
     */
    void setSelfMuted(boolean muted);

    /**
     * Whether connections from this AudioManager are muted,
     * if this is {@code true} packages by the registered {@link net.dv8tion.jda.api.audio.AudioSendHandler AudioSendHandler}
     * will be ignored by Discord.
     *
     * @return Whether connections from this AudioManager are muted
     */
    boolean isSelfMuted();

    /**
     * Sets whether connections from this AudioManager should be deafened.
     * <br>This does not include being muted, that value can be set individually from {@link #setSelfMuted(boolean)}
     * and checked via {@link #isSelfMuted()}
     *
     * @param deafened
     *        Whether connections from this AudioManager should be deafened.
     */
    void setSelfDeafened(boolean deafened);

    /**
     * Whether connections from this AudioManager are deafened.
     * <br>This does not include being muted, that value can be set individually from {@link #setSelfMuted(boolean)}
     * and checked via {@link #isSelfMuted()}
     *
     * @return True, if connections from this AudioManager are deafened
     */
    boolean isSelfDeafened();
}
