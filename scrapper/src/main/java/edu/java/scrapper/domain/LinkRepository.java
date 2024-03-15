package edu.java.scrapper.domain;

import edu.java.dto.Link;
import java.net.URI;
import java.util.List;

public interface LinkRepository {

    List<Link> findAll();

    void add(URI uri);

    int remove(URI uri);
}
