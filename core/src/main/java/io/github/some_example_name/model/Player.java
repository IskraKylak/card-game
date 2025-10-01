package io.github.some_example_name.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import io.github.some_example_name.model.data.DataCards;

public class Player extends Entity {
  private int maxMana;
  private int mana;
  private int startingHandSize = 4;

  private List<Card> defaultDeck;
  private List<Card> battleDeck;
  private List<Card> hand;
  private List<Card> discard;

  private final int maxHand;
  private final int maxUnits;
  private final Faction faction;

  private List<Slot> slots;

  public Player(int id, String name, int maxHealth, int maxMana, int maxHand, int maxUnits, Faction faction) {
    super(id, name, maxHealth);
    this.maxMana = maxMana;
    this.mana = maxMana;
    this.maxHand = maxHand;
    this.maxUnits = maxUnits;
    this.faction = faction;

    this.defaultDeck = new ArrayList<>();
    this.battleDeck = new ArrayList<>();
    this.hand = new ArrayList<>();
    this.discard = new ArrayList<>();

    this.slots = new ArrayList<>();
    for (int i = 0; i < maxUnits; i++) {
      this.slots.add(new Slot(i));
    }
  }

  // --- здоровье и мана ---
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

  // --- колоды и карты ---
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

  public boolean drawCard() {
    if (!battleDeck.isEmpty() && hand.size() < maxHand) {
      hand.add(battleDeck.remove(0));
      return true;
    }
    return false;
  }

  public void playCard(Card card) {
    if (hand.remove(card))
      discard.add(card);
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
          c.getEffect(),
          c.getImagePath());
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

  // --- слоты и юниты ---
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
    return false;
  }

  public Slot getFirstFreeSlot() {
    for (Slot slot : slots) {
      if (!slot.isOccupied())
        return slot;
    }
    return null;
  }

  // --- инициализация перед боем ---
  public void initBattle() {
    hand.clear();
    discard.clear();
    health = maxHealth;
    mana = maxMana;

    for (Slot slot : slots) {
      slot.removeUnit();
    }

    buildBattleDeck();

    for (int i = 0; i < startingHandSize; i++) {
      drawCard();
    }
  }

  public int getStartingHandSize() {
    return startingHandSize;
  }

  public void setStartingHandSize(int count) {
    this.startingHandSize += count;
  }

  public Faction getFaction() {
    return faction;
  }

  @Override
  public String getSpriteFolder() {
    return "player_sprites/"; // путь к спрайтам игрока
  }
}
