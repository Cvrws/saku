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
package net.dv8tion.jda.internal.managers;

import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class AudioManagerImpl implements AudioManager
{
    public final ReentrantLock CONNECTION_LOCK = new ReentrantLock();

    protected final GuildImpl guild;

    protected long queueTimeout = 100;
    protected boolean shouldReconnect = true;

    protected boolean selfMuted = false;
    protected boolean selfDeafened = false;

    protected long timeout = DEFAULT_CONNECTION_TIMEOUT;

    public AudioManagerImpl(GuildImpl guild)
    {
        this.guild = guild;
    }

    @Override
    public void openAudioConnection(AudioChannel channel)
    {
        Checks.notNull(channel, "Provided AudioChannel");

        if (!getGuild().equals(channel.getGuild()))
            throw new IllegalArgumentException("The provided AudioChannel is not a part of the Guild that this AudioManager handles." +
                    "Please provide a AudioChannel from the proper Guild");
        final Member self = getGuild().getSelfMember();


        checkChannel(channel, self);

        getJDA().getDirectAudioController().connect(channel);
    }

    private void checkChannel(AudioChannel channel, Member self)
    {
        EnumSet<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(channel.getPermissionContainer(), self));
        if (!perms.contains(Permission.VOICE_CONNECT))
            throw new InsufficientPermissionException(channel, Permission.VOICE_CONNECT);

        // if userLimit is 0 if no limit is set!
        final int userLimit = channel instanceof VoiceChannel ? ((VoiceChannel) channel).getUserLimit() : 0;
        if (userLimit > 0 && !perms.contains(Permission.ADMINISTRATOR))
        {
            // Check if we can actually join this channel
            // - If there is a userlimit
            // - If that userlimit is reached
            // - If we don't have voice move others permissions
            // VOICE_MOVE_OTHERS allows access because you would be able to move people out to
            // open up a slot anyway
            if (userLimit <= channel.getMembers().size()
                && !perms.contains(Permission.VOICE_MOVE_OTHERS))
            {
                throw new InsufficientPermissionException(channel, Permission.VOICE_MOVE_OTHERS,
                    "Unable to connect to AudioChannel due to userlimit! Requires permission VOICE_MOVE_OTHERS to bypass");
            }
        }
    }

    @Override
    public void closeAudioConnection()
    {
        getJDA().getAudioLifeCyclePool().execute(() -> {
            getJDA().setContext();
        });
    }

    @Nonnull
    @Override
    public JDAImpl getJDA()
    {
        return getGuild().getJDA();
    }

    @Nonnull
    @Override
    public GuildImpl getGuild()
    {
        return guild;
    }

    @Override
    public void setConnectTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    @Override
    public long getConnectTimeout()
    {
        return timeout;
    }

   
    @Override
    public void setAutoReconnect(boolean shouldReconnect)
    {
        this.shouldReconnect = shouldReconnect;
    }

    @Override
    public boolean isAutoReconnect()
    {
        return shouldReconnect;
    }

    @Override
    public void setSelfMuted(boolean muted)
    {
        if (selfMuted != muted)
        {
            this.selfMuted = muted;
            updateVoiceState();
        }
    }

    @Override
    public boolean isSelfMuted()
    {
        return selfMuted;
    }

    @Override
    public void setSelfDeafened(boolean deafened)
    {
        if (selfDeafened != deafened)
        {
            this.selfDeafened = deafened;
            updateVoiceState();
        }

    }

    @Override
    public boolean isSelfDeafened()
    {
        return selfDeafened;
    }

    public void setConnectedChannel(AudioChannel channel)
    {

    }

    public void setQueueTimeout(long queueTimeout)
    {

    }

    protected void updateVoiceState()
    {
        AudioChannel channel = getConnectedChannel();
        if (channel != null)
        {
            //This is technically equivalent to an audio open/move packet.
            getJDA().getDirectAudioController().connect(channel);
        }
    }

    @Override
    protected void finalize()
    {

    }

	@Override
	public AudioChannelUnion getConnectedChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
