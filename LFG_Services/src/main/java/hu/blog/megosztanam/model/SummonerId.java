package hu.blog.megosztanam.model;

import hu.blog.megosztanam.model.shared.summoner.Server;

/**
 * Created by Miklós on 2017. 05. 14..
 */
public class SummonerId {
    private Integer id;
    private Server server;

    public SummonerId() {
    }

    public SummonerId(Integer id, Server server) {
        this.id = id;
        this.server = server;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SummonerId that = (SummonerId) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return server == that.server;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (server != null ? server.hashCode() : 0);
        return result;
    }
}
