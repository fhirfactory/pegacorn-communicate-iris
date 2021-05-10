/*
 * Copyright (c) 2021 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.communicate.iris.matrixcontrol.workshops.matrixtasks.microtasks.matrixserver;

import net.fhirfactory.pegacorn.internals.matrix.r061.api.common.MAPIResponse;

public class UserMicroTasks {


    /**
     * This API sets the given user's display name. You must have permission to set this user's display name,
     * e.g. you need to have their access_token.
     *
     * @param userID
     * @param displayName
     * @return
     */
    public MAPIResponse setDisplayName(String userID, String displayName){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Get the user's display name. This API may be used to fetch the user's own displayname or to query the name of
     * other users; either locally or on remote homeservers.
     *
     * @param userID
     * @return
     */
    public MAPIResponse getDisplayName(String userID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * This API sets the given user's avatar URL. You must have permission to set this user's avatar URL,
     * e.g. you need to have their access_token.
     *
     * @param userID
     * @param avatarURL
     * @return
     */
    public MAPIResponse setAvatarURL(String userID, String avatarURL){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Get the user's avatar URL. This API may be used to fetch the user's own avatar URL or to query the URL of other
     * users; either locally or on remote homeservers.
     *
     * @param userID
     * @return
     */
    public MAPIResponse getAvatarURL(String userID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Get the combined profile information for this user. This API may be used to fetch the user's own profile
     * information or other users; either locally or on remote homeservers. This API may return keys which are not
     * limited to displayname or avatar_url.
     *
     * @param userID
     * @return
     */
    public MAPIResponse getUserProfile(String userID){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }
}
