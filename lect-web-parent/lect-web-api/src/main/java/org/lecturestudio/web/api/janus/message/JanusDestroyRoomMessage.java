/*
 * Copyright (C) 2021 TU Darmstadt, Department of Computer Science,
 * Embedded Systems and Applications Group.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lecturestudio.web.api.janus.message;

/**
 * Request container used to destroy a previously created video room.
 *
 * @author Alex Andres
 */
public class JanusDestroyRoomMessage extends JanusRoomRequest {

	private Boolean permanent;

	private String secret;


	/**
	 * Create a new {@code JanusDestroyRoomMessage}.
	 */
	public JanusDestroyRoomMessage() {
		setRequestType(JanusRoomRequestType.DESTROY);
	}

	/**
	 * Whether the room should be also removed from the config file on the
	 * server side. {@code False} by default.
	 *
	 * @return True to remove the room from the server config.
	 */
	public boolean isPermanent() {
		return permanent;
	}

	/**
	 * Set whether the room should be also removed from the config file on the
	 * server side. {@code False} by default.
	 *
	 * @param permanent True to remove the room from the server config.
	 */
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	/**
	 * Get the secret required to destroy the room.
	 *
	 * @return The required secret to destroy the room.
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * Set the secret required to destroy the room. This setting is mandatory if
	 * configured.
	 *
	 * @param secret The required secret to destroy the room.
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}
}
