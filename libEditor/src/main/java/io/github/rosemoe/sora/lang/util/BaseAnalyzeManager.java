/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2024  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.lang.util;

import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.text.ContentReference;

/**
 * Convenience base class for simple {@link AnalyzeManager} implementations
 *
 * @author Rosemoe
 */
public abstract class BaseAnalyzeManager implements AnalyzeManager {

    private StyleReceiver receiver;
    private ContentReference contentRef;
    private Bundle extraArguments;

    /**
     * Get current receiver, maybe null
     */
    @Nullable
    public StyleReceiver getReceiver() {
        return receiver;
    }

    @Override
    public void setReceiver(@Nullable StyleReceiver receiver) {
        this.receiver = receiver;
    }

    /**
     * Get current extra arguments, maybe null
     */
    @Nullable
    public Bundle getExtraArguments() {
        return extraArguments;
    }

    /**
     * Get current content reference, maybe null
     */
    @Nullable
    public ContentReference getContentRef() {
        return contentRef;
    }

    @Override
    @CallSuper
    public void reset(@NonNull ContentReference content, @NonNull Bundle extraArguments) {
        this.extraArguments = extraArguments;
        this.contentRef = content;
        rerun();
    }

    @Override
    @CallSuper
    public void destroy() {
        this.receiver = null;
        this.contentRef = null;
        this.extraArguments = null;
    }

}
