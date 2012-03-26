package org.jicengine.io;
import java.io.File;
import java.util.StringTokenizer;

/**
 *
 * <p>
 * Copyright (C) 2004  Timo Laitinen
 * </p>
 * @author Timo Laitinen
 * @created 2004-09-20
 * @since JICE-0.10
 */
public class PathResolver {

	private static final String DIRECTORY_SEPARATOR = "/";

	/**
	 * <p>
	 * Forms a real path from a base-path and a relative path.
	 * </p>
	 *
	 * <p>
	 * For example, <br>
	 * <code>'/directory/file1.txt' + 'file2.txt' -> '/directory/file2.txt'</code><br>
	 * <code>'/directory/file1.txt' + '../file2.txt' -> '/file2.txt'</code><br>
	 * <code>'/directory/subdir/' + 'file2.txt' -> '/directory/subdir/file2.txt'</code><br>
	 * <code>'/directory/file1.txt' + 'subdir/' -> '/directory/subdir/'</code><br>
	 * </p>
	 *
	 * @param contextPath
	 * @param relativePath
	 */
	public static String getRealPath(String contextPath, String relativePath)
	{
		File baseFile;
		if( contextPath.endsWith(DIRECTORY_SEPARATOR) || contextPath.endsWith(File.separator) ){
			// a directory
			baseFile = new File(contextPath);
		}
		else {
			// not a directory, so use the parent-file as the base-file.
			// the parent-file should be a directory..
			baseFile = new File(contextPath).getParentFile();
		}

		File newFile = new File(baseFile, relativePath);

		String realPath = getPathOf(newFile);

		// directories must end in '/'.
		// unfortunately the java.io.File discard the trailing '/'.
		// we'll add it manually.
		if( relativePath.endsWith(DIRECTORY_SEPARATOR) && !realPath.endsWith(DIRECTORY_SEPARATOR)){
			realPath = realPath + DIRECTORY_SEPARATOR;
		}

		return realPath;
	}

	public static String getPathOf(File file)
	{
		String path;
		if( File.separator.equals(DIRECTORY_SEPARATOR) ){
			// no need to do any conversions
			path = file.getPath();
		}
		else {
			// convert the windows file-separator to the unix-style that we are using.
			StringTokenizer tokenizer = new StringTokenizer(file.getPath(), File.separator, true);
			String token;
			StringBuffer pathBuffer = new StringBuffer();
			while(tokenizer.hasMoreElements() ){
				token = tokenizer.nextToken();
				if( token.equals(File.separator) ){
					pathBuffer.append(DIRECTORY_SEPARATOR);
				}
				else {
					pathBuffer.append(token);
				}
			}
			path = pathBuffer.toString();
		}

		return path;
	}

}
