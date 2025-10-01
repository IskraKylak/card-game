package io.github.some_example_name.core;

import io.github.some_example_name.model.Entity;

/**
 * Payload для события изменения здоровья сущности.
 */
public class EntityHealthChangedPayload {
  private final Entity entity;

  public EntityHealthChangedPayload(Entity entity) {
    this.entity = entity;
  }

  public Entity getEntity() {
    return entity;
  }
}
