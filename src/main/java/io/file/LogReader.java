package io.file;

import io.LogObject;

import java.util.List;

public interface LogReader {

    /**
     * Get new logs from reader
     * @return list at most limit origin log
     */
    public List<LogObject> readNewLogs();

    /**
     * Get new logs from reader
     * @param limit limit max number log want to get
     * @return list at most limit origin log
     */
    public List<LogObject> readNewLogs(int limit);
}
