/*
 * Copyright © 2011 Sattvik Software & Technology Resources, Ltd. Co.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.sattvik.baitha.test;

import android.os.Parcel;
import android.os.Parcelable;

public class DummyParcelable implements Parcelable {
    private int data;

    public DummyParcelable() {
        data = 42;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(data);
    }

    public static final Creator<DummyParcelable> CREATOR =
            new  Creator<DummyParcelable>() {
                @Override
                public DummyParcelable createFromParcel(Parcel in) {
                    return new DummyParcelable(in);
                }

                @Override
                public DummyParcelable[] newArray(int size) {
                    return new DummyParcelable[size];
                }
            };

    private DummyParcelable(Parcel in) {
        data = in.readInt();
    }
}
