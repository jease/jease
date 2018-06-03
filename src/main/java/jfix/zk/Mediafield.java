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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import jfix.util.I18N;
import jfix.util.MimeTypes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.image.AImage;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Vbox;

public class Mediafield extends Vbox {

    private boolean preview;
    private Media media = null;
    private Codemirror codemirror = new Codemirror();
    private Image image = new Image();
    private Button rotateImage = new Button(I18N.get("Rotate"), Images.EditRedo);
    private Spinner width = new Spinner();
    private Spinner height = new Spinner();
    private Checkbox aspectRatioLock = new Checkbox();
    private Column imagePreview = new Column(image, new Row(width,
            aspectRatioLock, height, rotateImage));
    private Fileupload upload = new Fileupload();
    private Button download = new Button();
    private Dimension originalDimension = null;

    public Mediafield() {
        setHflex("1");
        upload.addEventListener(Events.ON_UPLOAD,
                $event -> uploadPerformed(((UploadEvent) $event).getMedia()));
        download.addEventListener(Events.ON_CLICK,
                $event -> downloadPerformed());
        rotateImage.addEventListener(
                Events.ON_CLICK,
                $event -> {
                    try {
                        File imageFile = Medias.asFile(media);
                        imageFile.deleteOnExit();
                        File rotatedImage = jfix.util.Images.rotate(imageFile);
                        rotatedImage.deleteOnExit();
                        setMedia(media.getName(), media.getContentType(),
                                rotatedImage);
                    } catch (IOException e) {
                        Modal.exception(e);
                    }

                });

        aspectRatioLock.setChecked(true);
        EventListener<Event> dimensionChanged = $event -> {
            Dimension scaledDimension = null;
            if (aspectRatioLock.isChecked()) {
                scaledDimension = jfix.util.Images.scaleDimension(
                        originalDimension, new Dimension(width.intValue(),
                                height.intValue()));
                width.setValue((int) scaledDimension.getWidth());
                height.setValue((int) scaledDimension.getHeight());
            } else {
                scaledDimension = new Dimension(width.intValue(),
                        height.intValue());
            }
            File imageFile = Medias.asFile(media);
            imageFile.deleteOnExit();
            File scaledImage = jfix.util.Images.scale(imageFile,
                    scaledDimension);
            scaledImage.deleteOnExit();
            setMedia(media.getName(), media.getContentType(), scaledImage);
        };
        width.addEventListener(Events.ON_CHANGE, dimensionChanged);
        height.addEventListener(Events.ON_CHANGE, dimensionChanged);

        setHeight("300px");
        setPreview(true);
        codemirror.setVisible(false);
        imagePreview.setVisible(false);
        width.setStep(10);
        height.setStep(10);

        download.setImage(Images.DriveRemovableMedia);
        download.setVisible(false);

        appendChild(codemirror);
        appendChild(imagePreview);
        appendChild(new Row(download, upload));
    }

    public void setHeight(String height) {
        codemirror.setHeight(height);
        image.setHeight(height);
    }

    private void uploadPerformed(Media uploadedMedia) {
        if (uploadedMedia != null) {
            this.media = uploadedMedia;
            Events.sendEvent(new Event(Events.ON_UPLOAD, this));
            updateMediaViewer();
        }
    }

    private void downloadPerformed() {
        Filedownload.save(media);
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(String name, String contentType, File file) {
        try {
            if (file != null && file.exists()) {
                setMedia(new AMedia(name, null, contentType, file, true));
            } else {
                setMedia(null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setMedia(Media media) {
        this.media = media;
        updateMediaViewer();
    }

    private void updateMediaViewer() {
        try {
            download.setVisible(media != null);
            imagePreview.setVisible(false);
            codemirror.setVisible(false);
            download.setLabel(I18N.get("Download") + " (" + getContentType()
                    + ")");
            if (isPreview()) {
                if (getContentType() != null) {
                    if (getContentType().startsWith("image")) {
                        InputStream input = Medias.asStream(media);
                        imagePreview.setVisible(true);
                        image.setContent(new AImage(getName(), input));
                        input.close();
                        adjustImage();
                    }
                    if (isContentTypeEditable()) {
                        String mediaString = Medias.asString(media);
                        if (mediaString.length() < 1024 * 1024) {
                            codemirror.setVisible(true);
                            codemirror.setValue(Medias.asString(media));
                            codemirror.setSyntax(FilenameUtils.getExtension(media.getName()));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean isEmpty() {
        return media == null;
    }

    public void copyToFile(File target) {
        if (target == null) {
            return;
        }
        try {
            if (codemirror.isVisible()) {
                FileUtils.writeStringToFile(target, codemirror.getValue(), "UTF-8");
                codemirror.setSyntax(FilenameUtils.getExtension(target.getName()));
            } else {
                if (media != null) {
                    Files.move(Medias.asFile(media).toPath(), target.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getContentType() {
        if (media == null) {
            return null;
        }
        if (media.getContentType() == null
                || media.getContentType().equals("application/octet-stream")) {
            return MimeTypes.guessContentTypeFromName(media.getName());
        } else {
            return media.getContentType();
        }
    }

    private boolean isContentTypeEditable() {
        String ctype = getContentType();
        return ctype != null
                && (ctype.startsWith("text") || ctype
                .equals("application/javascript"));
    }

    public String getName() {
        return media != null ? media.getName() : null;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isPreview() {
        return preview;
    }

    private void adjustImage() {
        try {
            int textHeight = Integer.parseInt(codemirror.getHeight().replace(
                    "px", ""));
            File imageFile = Medias.asFile(media);
            imageFile.deleteOnExit();
            originalDimension = jfix.util.Images.getSize(imageFile);
            width.setValue((int) originalDimension.getWidth());
            height.setValue((int) originalDimension.getHeight());
            int imageHeight = (int) originalDimension.getHeight();
            int minHeight = Math.min(imageHeight, textHeight);
            image.setHeight(minHeight + "px");
        } catch (Throwable e) {
            // pass
        }
    }

    public void setUploadLimit(String limit) {
        upload.setUploadLimit(limit);
    }
}
