package hu.blog.megosztanam.login;

import hu.blog.megosztanam.model.shared.Summoner;

public interface ISummonerResponseHandler {

    void notFound();

    void found(Summoner summoner);
}
