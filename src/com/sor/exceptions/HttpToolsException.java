package com.sor.exceptions;

public class HttpToolsException extends Exception
{
      /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Parameterless Constructor
      public HttpToolsException() {}

      //Constructor that accepts a message
      public HttpToolsException(String message)
      {
         super(message);
      }
 }
