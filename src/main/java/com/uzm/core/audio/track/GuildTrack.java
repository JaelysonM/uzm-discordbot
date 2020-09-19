/*
 * (C) Copyright 2016 Dinos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uzm.core.audio.track;

import net.dv8tion.jda.api.entities.Member;
import java.util.HashSet;
import java.util.Set;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;


public class GuildTrack {

  private final AudioTrack track;
  private final Set<String> skips;
  private final Guild guild;
  private final Member author;

  public GuildTrack(AudioTrack track, Guild guild, Member author) {
    this.track = track;
    this.skips = new HashSet<>();
    this.guild = guild;
    this.author= author;
  }

  public AudioTrack getTrack() {
    return track;
  }

  public Member getAuthor() {
    return author;
  }

  public int getSkips() {
    return skips.size();
  }

  public void addSkip(User u) {
    skips.add(u.getId());
  }

  public boolean hasVoted(User u) {
    return skips.contains(u.getId());
  }

  public Guild getGuild() {
    return guild;
  }


}