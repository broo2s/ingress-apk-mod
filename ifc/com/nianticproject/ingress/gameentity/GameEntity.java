package com.nianticproject.ingress.gameentity;

public interface GameEntity {

    public String getGuid();

    public EntityComponent getComponent(Class<?> type);
}
