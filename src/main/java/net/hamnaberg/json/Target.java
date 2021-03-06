package net.hamnaberg.json;

import java.net.URI;
import java.util.List;

public interface Target {
    public URI expand(Iterable<Property> properties);
    public String toString();
    public URI toURI();
    public boolean isURITemplate();
}
