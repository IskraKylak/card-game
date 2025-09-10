package io.github.some_example_name.core;

import java.util.ArrayList;
import java.util.List;

import io.github.some_example_name.model.Card;
import io.github.some_example_name.model.Slot;

public class GameState {

  public enum Turn {
    PLAYER,
    PLAYER_UNITS,
    ENEMY
  }

  private Turn currentTurn;

  private int currentMana;
  private List<Card> hand; // карты в руке
  private List<Card> discard; // карты в отбой
  private List<Slot> slots; // слоты на доске (для юнитов игрока)

  public GameState(List<Slot> boardSlots, int initialMana) {
    this.slots = boardSlots;
    this.currentMana = initialMana;
    this.hand = new ArrayList<>();
    this.discard = new ArrayList<>();
    this.currentTurn = Turn.PLAYER;
  }

  // --- Геттеры / Сеттеры ---
  public Turn getCurrentTurn() {
    return currentTurn;
  }

  public void setCurrentTurn(Turn turn) {
    this.currentTurn = turn;
  }

  public int getCurrentMana() {
    return currentMana;
  }

  public void setCurrentMana(int mana) {
    this.currentMana = mana;
  }

  public List<Card> getHand() {
    return hand;
  }

  public void setHand(List<Card> hand) {
    this.hand = hand;
  }

  public List<Card> getDiscard() {
    return discard;
  }

  public void setDiscard(List<Card> discard) {
    this.discard = discard;
  }

  public List<Slot> getSlots() {
    return slots;
  }

  public void setSlots(List<Slot> slots) {
    this.slots = slots;
  }

  // --- Помощники ---
  public void addCardToHand(Card card) {
    hand.add(card);
  }

  public void discardCard(Card card) {
    if (hand.remove(card)) {
      discard.add(card);
    }
  }

  public void resetHandAndDiscard() {
    hand.clear();
    discard.clear();
  }
}
