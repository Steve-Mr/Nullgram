/*
 * Copyright (C) 2019-2024 qwq233 <qwq233@qwq2333.top>
 * https://github.com/qwq233/Nullgram
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this software.
 *  If not, see
 * <https://www.gnu.org/licenses/>
 */

package org.telegram.ui.bots;


import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class WebViewRequestProps {

    public int currentAccount;
    public long peerId;
    public long botId;
    public String buttonText;
    public String buttonUrl;
    public @BotWebViewAttachedSheet.WebViewType int type;
    public int replyToMsgId;
    public boolean silent;
    public TLRPC.BotApp app;
    public boolean allowWrite;
    public String startParam;
    public TLRPC.User botUser;
    public int flags;
    public boolean compact;

    public TLObject response;
    public long responseTime;


    public static WebViewRequestProps of(
            int currentAccount,
            long peerId,
            long botId,
            String buttonText,
            String buttonUrl,
            @BotWebViewAttachedSheet.WebViewType int type,
            int replyToMsgId,
            boolean silent,
            TLRPC.BotApp app,
            boolean allowWrite,
            String startParam,
            TLRPC.User botUser,
            int flags,
            boolean compact
    ) {
        WebViewRequestProps p = new WebViewRequestProps();
        p.currentAccount = currentAccount;
        p.peerId = peerId;
        p.botId = botId;
        p.buttonText = buttonText;
        p.buttonUrl = buttonUrl;
        p.type = type;
        p.replyToMsgId = replyToMsgId;
        p.silent = silent;
        p.app = app;
        p.allowWrite = allowWrite;
        p.startParam = startParam;
        p.botUser = botUser;
        p.flags = flags;
        p.compact = compact;
        if (!compact && !TextUtils.isEmpty(buttonUrl)) {
            try {
                Uri uri = Uri.parse(buttonUrl);
                p.compact = TextUtils.equals(uri.getQueryParameter("mode"), "compact");
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return p;
    }

    public void applyResponse(TLObject response) {
        this.response = response;
        this.responseTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof WebViewRequestProps))
            return false;
        final WebViewRequestProps p = (WebViewRequestProps) obj;
        return (
                currentAccount == p.currentAccount &&
                        peerId == p.peerId &&
                        botId == p.botId &&
                        TextUtils.equals(buttonUrl, p.buttonUrl) &&
                        type == p.type &&
                        replyToMsgId == p.replyToMsgId &&
                        silent == p.silent &&
                        (app == null ? 0 : app.id) == (p.app == null ? 0 : p.app.id) &&
                        allowWrite == p.allowWrite &&
                        TextUtils.equals(startParam, p.startParam) &&
                        (botUser == null ? 0 : botUser.id) == (p.botUser == null ? 0 : p.botUser.id) &&
                        flags == p.flags
        );
    }
}