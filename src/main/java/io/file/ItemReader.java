package io.file;

import datastruct.Item;
import io.OriginItem;

import java.util.List;

public interface ItemReader {
    List<OriginItem> read();
    List<Item> readObject();
}
