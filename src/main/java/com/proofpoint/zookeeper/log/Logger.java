package com.proofpoint.zookeeper.log;

/**
 */
public class Logger
{
  private final org.apache.log4j.Logger log;

  public static Logger get(Class clazz)
  {
    return new Logger(clazz);
  }

  public static Logger get(String name)
  {
    return new Logger(name);
  }

  public Logger(Class clazz) {
    log = org.apache.log4j.Logger.getLogger(clazz);
  }

  public Logger(String name) {
    log = org.apache.log4j.Logger.getLogger(name);
  }

  public void debug(String message, Object... formatArgs)
  {
    if (log.isDebugEnabled()) {
      log.debug(String.format(message, formatArgs));
    }
  }

  public void debug(Throwable t, String message, Object... formatArgs)
  {
    if (log.isDebugEnabled()) {
      log.debug(String.format(message, formatArgs), t);
    }
  }

  public void info(String message, Object... formatArgs)
  {
    if (log.isInfoEnabled()) {
      log.info(String.format(message, formatArgs));
    }
  }

  public void info(Throwable t, String message, Object... formatArgs)
  {
    if (log.isInfoEnabled()) {
      log.info(String.format(message, formatArgs), t);
    }
  }

  public void warn(String message, Object... formatArgs)
  {
    log.warn(String.format(message, formatArgs));
  }

  public void warn(Throwable t, String message, Object... formatArgs)
  {
    log.warn(String.format(message, formatArgs), t);
  }

  public void error(String message, Object... formatArgs)
  {
    log.error(String.format(message, formatArgs));
  }

  public void error(Throwable t, String message, Object... formatArgs)
  {
    log.error(String.format(message, formatArgs), t);
  }

  public void fatal(String message, Object... formatArgs)
  {
    log.fatal(String.format(message, formatArgs));
  }

  public void fatal(Throwable t, String message, Object... formatArgs)
  {
    log.fatal(String.format(message, formatArgs), t);
  }

  public boolean isDebugEnabled()
  {
    return log.isDebugEnabled();
  }

  public boolean isInfoEnabled()
  {
    return log.isInfoEnabled();
  }
}
