package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.some_example_name.model.data.DataCards;

public class Player {
  private int maxHealth;
  private int health;

  private int maxMana;
  private int mana;

  // Колоды
  private List<Card> defaultDeck; // шаблонная колода персонажа
  private List<Card> battleDeck; // колода для текущей битвы
  private List<Card> hand;
  private List<Card> discard;

  private final int maxHand; // макс. карт в руке
  private final int maxUnits; // макс. юнитов на поле (количество слотов)
  private final Faction faction; // фракция игрока

  // Слоты для юнитов
  private List<Slot> slots;

  public Player(int maxHealth, int maxMana, int maxHand, int maxUnits, Faction faction) {
    this.maxHealth = maxHealth;
    this.health = maxHealth;
    this.maxMana = maxMana;
    this.mana = maxMana;

    this.maxHand = maxHand;
    this.maxUnits = maxUnits;
    this.faction = faction;

    this.defaultDeck = new ArrayList<>();
    this.battleDeck = new ArrayList<>();
    this.hand = new ArrayList<>();
    this.discard = new ArrayList<>();

    // создаём слоты
    this.slots = new ArrayList<>();
    for (int i = 0; i < maxUnits; i++) {
      this.slots.add(new Slot(i));
    }
  }

  // --- здоровье ---
  public int getHealth() {
    return health;
  }

  public void setHealth(int hp) {
    this.health = Math.max(0, Math.min(hp, maxHealth));
  }

  public void takeDamage(int dmg) {
    this.health = Math.max(0, health - dmg);
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  // --- мана ---
  public int getMana() {
    return mana;
  }

  public void setMana(int mana) {
    this.mana = Math.max(0, Math.min(mana, maxMana));
  }

  public void restoreMana(int amount) {
    this.mana = Math.min(maxMana, mana + amount);
  }

  public int getMaxMana() {
    return maxMana;
  }

  // --- карты ---
  public List<Card> getDefaultDeck() {
    return defaultDeck;
  }

  public List<Card> getBattleDeck() {
    return battleDeck;
  }

  public List<Card> getHand() {
    return hand;
  }

  public List<Card> getDiscard() {
    return discard;
  }

  public int getMaxHand() {
    return maxHand;
  }

  public void buildDefaultDeckFromFaction() {
    defaultDeck.clear();
    Random rnd = new Random();
    List<Card> factionCards = DataCards.createFactionCards(faction);

    for (int i = 0; i < 10; i++) {
      Card c = factionCards.get(rnd.nextInt(factionCards.size()));
      Card copy = new Card(
          c.getId(),
          c.getName(),
          c.getDescription(),
          c.getCost(),
          c.getType(),
          c.getFaction(),
          c.getEffect());
      defaultDeck.add(copy);
    }
  }

  public void buildBattleDeck() {
    battleDeck.clear();
    for (Card c : defaultDeck) {
      battleDeck.add(c);
    }
    Collections.shuffle(battleDeck);
  }

  // Player.java
  public boolean drawCard() {
    // Просто берём карту из колоды, не трогаем отбой
    if (!battleDeck.isEmpty() && hand.size() < maxHand) {
      hand.add(battleDeck.remove(0));
      return true;
    }
    return false;
  }

  public void discardCard(Card card) {
    if (hand.remove(card))
      discard.add(card);
  }

  public void playCard(Card card) {
    if (hand.remove(card))
      discard.add(card);
  }

  // --- слоты / юниты ---
  public List<Slot> getSlots() {
    return slots;
  }

  public boolean summonUnit(Unit unit) {
    for (Slot slot : slots) {
      if (!slot.isOccupied()) {
        slot.setUnit(unit);
        return true;
      }
    }
    return false; // нет свободного слота
  }

  public Slot getFirstFreeSlot() {
    for (Slot slot : slots) {
      if (!slot.isOccupied()) {
        return slot;
      }
    }
    return null;
  }

  // --- сброс перед новым боем ---
  public void initBattleDeck() {
    hand.clear();
    discard.clear();
    health = maxHealth;
    mana = maxMana;

    // очищаем слоты
    for (Slot slot : slots) {
      slot.removeUnit();
    }
  }

  public Faction getFaction() {
    return faction;
  }
}
