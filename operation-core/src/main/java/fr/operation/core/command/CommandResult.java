package fr.operation.core.command;

public class CommandResult {

	private int exitValue;
	private String outputStream;
	private String errorStream;
	
	public CommandResult(int exitValue, String outputStream, String errorStream) {
		super();
		this.exitValue = exitValue;
		this.outputStream = outputStream;
		this.errorStream = errorStream;
	}
	
	public int getExitValue() {
		return exitValue;
	}	
	public String getOutputStream() {
		return outputStream;
	}	
	public String getErrorStream() {
		return errorStream;
	}	
}
