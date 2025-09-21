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
  private int startingHandSize = 4;

  // Колоды
  private List<Card> defaultDeck; // шаблонная колода персонажа
  private List<Card> battleDeck; // колода для текущей битвы
  private List<Card> hand; // карты на руке
  private List<Card> discard; // сброс

  private final int maxHand; // макс. карт в руке
  private final int maxUnits; // макс. юнитов на поле (количество слотов)
  private final Faction faction; // фракция игрока

  // Слоты для юнитов
  private List<Slot> slots;

  /**
   * Конструктор Player
   * Инициализирует здоровье, ману, фракцию, колоды и слоты
   */
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

    // создаём слоты для юнитов
    this.slots = new ArrayList<>();
    for (int i = 0; i < maxUnits; i++) {
      this.slots.add(new Slot(i));
    }
  }

  /**
   * Инициализация игрока перед битвой
   * Очищает руку и сброс, восстанавливает здоровье и ману, очищает слоты
   * Создает боевую колоду и берет стартовую руку
   */
  public void initBattle() {
    hand.clear();
    discard.clear();
    health = maxHealth;
    mana = maxMana;

    // очищаем слоты
    for (Slot slot : slots) {
      slot.removeUnit();
    }

    // создаём боевую колоду из дефолтной
    battleDeck.clear();
    for (Card c : defaultDeck) {
      battleDeck.add(c);
    }
    Collections.shuffle(battleDeck);

    // берём стартовую руку
    for (int i = 0; i < startingHandSize; i++) {
      drawCard();
    }
  }

  // --- ЗДОРОВЬЕ ---

  /** Получить текущее здоровье */
  public int getHealth() {
    return health;
  }

  /** Получить стартовый размер руки */
  public int getStartingHandSize() {
    return startingHandSize;
  }

  /** Увеличить стартовый размер руки на count */
  public void setStartingHandSize(int count) {
    this.startingHandSize = this.startingHandSize + count;
  }

  /** Установить здоровье игрока */
  public void setHealth(int hp) {
    this.health = Math.max(0, Math.min(hp, maxHealth));
  }

  /** Нанести урон игроку */
  public void takeDamage(int dmg) {
    this.health = Math.max(0, health - dmg);
  }

  /** Получить максимальное здоровье */
  public int getMaxHealth() {
    return maxHealth;
  }

  // --- МАНА ---

  /** Получить текущее количество маны */
  public int getMana() {
    return mana;
  }

  /** Установить текущее количество маны */
  public void setMana(int mana) {
    this.mana = Math.max(0, Math.min(mana, maxMana));
  }

  /** Восстановить ману на указанное количество */
  public void restoreMana(int amount) {
    this.mana = Math.min(maxMana, mana + amount);
  }

  /** Получить максимальное количество маны */
  public int getMaxMana() {
    return maxMana;
  }

  // --- КАРТЫ ---

  /** Получить дефолтную колоду игрока */
  public List<Card> getDefaultDeck() {
    return defaultDeck;
  }

  /** Получить боевую колоду для текущей битвы */
  public List<Card> getBattleDeck() {
    return battleDeck;
  }

  /** Получить карты на руке */
  public List<Card> getHand() {
    return hand;
  }

  /** Получить карты в сбросе */
  public List<Card> getDiscard() {
    return discard;
  }

  /** Получить максимальное количество карт на руке */
  public int getMaxHand() {
    return maxHand;
  }

  /**
   * Создает дефолтную колоду игрока на основе фракции
   * Берет случайные карты фракции
   */
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

  /** Копирует дефолтную колоду в боевую и тасует её */
  public void buildBattleDeck() {
    battleDeck.clear();
    for (Card c : defaultDeck) {
      battleDeck.add(c);
    }
    Collections.shuffle(battleDeck);
  }

  /**
   * Берет верхнюю карту из боевой колоды и добавляет в руку
   * Возвращает true, если карта успешно взята
   */
  public boolean drawCard() {
    if (!battleDeck.isEmpty() && hand.size() < maxHand) {
      hand.add(battleDeck.remove(0));
      return true;
    }
    return false;
  }

  /** Убирает карту из руки и помещает её в сброс (карта сыграна) */
  public void playCard(Card card) {
    if (hand.remove(card))
      discard.add(card);
  }

  // --- СЛОТЫ И ЮНИТЫ ---

  /** Получить список слотов игрока */
  public List<Slot> getSlots() {
    return slots;
  }

  /** Ставит юнита в первый свободный слот на поле */
  public boolean summonUnit(Unit unit) {
    for (Slot slot : slots) {
      if (!slot.isOccupied()) {
        slot.setUnit(unit);
        return true;
      }
    }
    return false;
  }

  /** Получить первый свободный слот */
  public Slot getFirstFreeSlot() {
    for (Slot slot : slots) {
      if (!slot.isOccupied()) {
        return slot;
      }
    }
    return null;
  }

  /**
   * Подготавливает игрока к битве (очистка руки, сброса, слотов и восстановление
   * здоровья/маны)
   */
  public void initBattleDeck() {
    hand.clear();
    discard.clear();
    health = maxHealth;
    mana = maxMana;

    for (Slot slot : slots) {
      slot.removeUnit();
    }
  }

  /** Получить фракцию игрока */
  public Faction getFaction() {
    return faction;
  }
}
