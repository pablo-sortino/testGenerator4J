package generator;

public abstract class SimpleLogger {

	/**
	 * Flag for verbose mode.
	 */
	private static boolean verbose = false;

	public static boolean isVerbose() {
		return verbose;
	}

	public static void setVerbose(boolean verbose) {
		SimpleLogger.verbose = verbose;
	}

	/**
	 * Print a warning message.
	 */
	public static void printWarningS(String s) {
		System.out.println("warning: " + s);
	}

	/**
	 * Print an error message.
	 */
	public static void printErrorS(String s) {
		System.out.println("error: " + s);
	}

	/**
	 * Print a 'normal' message.
	 */
	public static void printNoticeS(String s) {
		log(s);
	}

	/**
	 * Output a message to the console, if in verbose mode. If in non-verbose
	 * mode, this does nothing.
	 */
	public static void log(String s) {
		if (verbose) {
			System.out.println(s);
		}
	}

	/**
	 * Output an error message to the console and exit the program.
	 */
	public static void error(String s) {
		System.out.println("error: " + s);
		System.exit(1);
	}

	/**
	 * Output an error message and an exception's/error's message and exit the
	 * program.
	 */
	public static void error(String s, Throwable t) {
		error(s + " - " + t.getMessage());
	}

	/**
	 * Print a warning message. Same as printWarningS but callable via
	 * non-static instance of DocErrorReporter.
	 */
	public void printWarning(String s) {
		printWarningS(s);
	}

	/**
	 * Print an error message. Same as printErrorS but callable via non-static
	 * instance of DocErrorReporter.
	 */
	public void printError(String s) {
		printErrorS(s);
	}

	/**
	 * Print a 'normal' message. Same as printNoticeS but callable via
	 * non-static instance of DocErrorReporter.
	 */
	public void printNotice(String s) {
		printNoticeS(s);
	}
}
