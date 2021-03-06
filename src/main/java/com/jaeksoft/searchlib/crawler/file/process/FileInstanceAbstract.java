/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2010-2014 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.crawler.file.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.crawler.file.database.FilePathItem;
import com.jaeksoft.searchlib.crawler.file.database.FileTypeEnum;
import com.jaeksoft.searchlib.util.IOUtils;

public abstract class FileInstanceAbstract {

	protected FileInstanceAbstract parent;

	protected FilePathItem filePathItem;

	private String path;

	private URI uri;

	final public static FileInstanceAbstract create(FilePathItem filePathItem, FileInstanceAbstract parent, String path)
			throws SearchLibException {
		FileInstanceAbstract fileInstance;
		try {
			fileInstance = filePathItem.getType().getNewInstance();
			fileInstance.init(filePathItem, parent, path);
			return fileInstance;
		} catch (InstantiationException e) {
			throw new SearchLibException(e);
		} catch (IllegalAccessException e) {
			throw new SearchLibException(e);
		} catch (URISyntaxException e) {
			throw new SearchLibException(e);
		} catch (UnsupportedEncodingException e) {
			throw new SearchLibException(e);
		}
	}

	protected FileInstanceAbstract() {
	}

	final protected void init(FilePathItem filePathItem, FileInstanceAbstract parent, String path)
			throws URISyntaxException, SearchLibException, UnsupportedEncodingException {
		this.filePathItem = filePathItem;
		this.parent = parent;
		this.path = path;
		this.uri = init();
	}

	public abstract URI init() throws SearchLibException, URISyntaxException, UnsupportedEncodingException;

	public URI getURI() {
		return uri;
	}

	public abstract FileTypeEnum getFileType() throws SearchLibException;

	public abstract FileInstanceAbstract[] listFilesAndDirectories()
			throws URISyntaxException, SearchLibException, UnsupportedEncodingException;

	public abstract FileInstanceAbstract[] listFilesOnly()
			throws URISyntaxException, SearchLibException, UnsupportedEncodingException;

	public abstract String getFileName() throws SearchLibException;

	public abstract Long getLastModified() throws SearchLibException;

	public abstract Long getFileSize() throws SearchLibException;

	public abstract InputStream getInputStream() throws IOException;

	public abstract void delete() throws IOException;

	public String check() throws SearchLibException, URISyntaxException, UnsupportedEncodingException {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			int limit = 10;
			FileInstanceAbstract[] files = listFilesAndDirectories();
			if (files != null)
				for (FileInstanceAbstract file : files) {
					pw.println(file.getPath());
					if (--limit == 0) {
						pw.println("...");
						break;
					}
				}
			return sw.toString();
		} finally {
			IOUtils.close(pw, sw);
		}
	}

	public FilePathItem getFilePathItem() {
		return filePathItem;
	}

	public FileInstanceAbstract getParent() {
		return parent;
	}

	public String getPath() {
		return path;
	}

	public static interface SecurityInterface {

		public List<SecurityAccess> getSecurity() throws IOException;

	}

	public String getURL() throws MalformedURLException {
		if (uri == null)
			return null;
		return uri.toASCIIString();
	}

}
