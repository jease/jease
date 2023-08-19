/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jfix.zk;

import org.apache.commons.lang3.StringUtils;

import jfix.util.I18N;

public class Fileupload extends org.zkoss.zul.Button {

    private boolean multiple;
    private int limit;

    public Fileupload() {
        this(I18N.get("Upload"), Images.UserHome);
    }

    public Fileupload(String label, String image) {
        this(label, image, false/*multiple*/);
    }

    public Fileupload(String label, String image, boolean multiple) {
        super(label, image);
        this.multiple = multiple;
        configure();
    }

    private void configure() {
        String s = "";
        if (multiple) s += ",multiple=true";
        if (limit != 0) s += ",maxsize=" + String.valueOf(limit);
        setUpload("true" + s);
    }

    public int getLimit() {
        return limit;
    }

    public void setUploadLimit(String limit) {
        if (StringUtils.isNotBlank(limit)) {
            try {
                this.limit = Integer.parseInt(limit);
            } catch (Exception ex) {
                this.limit = 0;
            }
        } else {
            this.limit = 0;
        }
        configure();
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
        configure();
    }
}
