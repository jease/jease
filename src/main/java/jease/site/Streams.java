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
package jease.site;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.Names;
import jease.Registry;
import jfix.util.Images;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Service to stream binary contents to client.
 */
public class Streams {

	/**
	 * Write given file to response.
	 * 
	 * If the given content type denotes a browser supported image, the image
	 * will be automatically scaled if either "scale" is present as request
	 * paramter or JEASE_IMAGE_LIMIT is set in Registry.
	 */
	public static void write(HttpServletRequest request, HttpServletResponse response, File file, String contentType)
			throws IOException {
		if (Images.isBrowserCompatible(contentType)) {
			int scale = NumberUtils.toInt(request.getParameter("scale"));
			if (scale > 0) {
				java.io.File scaledImage = Images.scale(file, scale);
				scaledImage.deleteOnExit();
				response.setContentType(contentType);
				response.setContentLength((int) scaledImage.length());
				Files.copy(scaledImage.toPath(), response.getOutputStream());
				return;
			}
			int limit = NumberUtils.toInt(Registry
					.getParameter(Names.JEASE_IMAGE_LIMIT));
			if (limit > 0) {
				java.io.File scaledImage = Images.limit(file, limit);
				scaledImage.deleteOnExit();
				response.setContentType(contentType);
				response.setContentLength((int) scaledImage.length());
				Files.copy(scaledImage.toPath(), response.getOutputStream());
				return;
			}

		}
		response.setContentType(contentType);
		response.setContentLength((int) file.length());
		Files.copy(file.toPath(), response.getOutputStream());
	}
}