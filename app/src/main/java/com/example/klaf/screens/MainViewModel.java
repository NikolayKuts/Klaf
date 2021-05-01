package com.example.klaf.screens;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.klaf.data.KlafDatabase;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private static KlafDatabase database;
    private LiveData<List<Deck>> decks;
    private LiveData<List<Card>> cards;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = KlafDatabase.getInstance(application);
        decks = database.deckDao().getAllDecks();
        cards = database.cardDao().getAllCards();
    }

    public LiveData<List<Deck>> getDecks() {
        return decks;
    }

    public LiveData<List<Card>> getCards() {
        return cards;
    }

    public void insertDeck(Deck deck) {
        new InsertDeckTask().execute(deck);
    }
    private static class InsertDeckTask extends AsyncTask<Deck, Void, Void> {
        @Override
        protected Void doInBackground(Deck... decks) {
            database.deckDao().insetDeck(decks[0]);
            return null;
        }
    }

    public void insertDeckList(List<Deck> decks) {
        new InsertDeckListTask().execute(decks);
    }
    private static class InsertDeckListTask extends AsyncTask<List<Deck>, Void, Void> {
        @Override
        protected Void doInBackground(List<Deck>... lists) {
            database.deckDao().insertDeckList(lists[0]);
            return null;
        }
    }

    public Deck getDeckById(int id) {
        Deck result = null;
        try {
            result = new GetDeckByIdTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetDeckByIdTask extends AsyncTask<Integer, Void, Deck> {
        @Override
        protected Deck doInBackground(Integer... integers) {
            return database.deckDao().getDeckById(integers[0]);
        }
    }

    public List<Deck> getDeckList() {
        List<Deck> result = null;
        try {
            result = new GetDeckListTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static class GetDeckListTask extends AsyncTask<Void, Void, List<Deck>> {
        @Override
        protected List<Deck> doInBackground(Void... voids) {
            return database.deckDao().getDeckList();
        }
    }

    public void removeDeck(Deck deck) {
        new RemoveDeckTask().execute(deck);
    }

    private static class RemoveDeckTask extends AsyncTask<Deck, Void, Void> {
        @Override
        protected Void doInBackground(Deck... decks) {
            database.deckDao().deleteDeck(decks[0]);
            return null;
        }
    }

    public List<Integer> getCardQuantityList() {
        List<Integer> result = new ArrayList<>();
        List<Deck> current;
        try {
            current = new GetDeckListTask().execute().get();

            for (Deck deck : current) {
                int quantity = new GetCardQuantityByDeckId().execute(deck.getId()).get();
                result.add(quantity);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getCardQuantityByDeckId(int deckId) {
        int result = 0;
        try {
            result = new GetCardQuantityByDeckId().execute(deckId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetCardQuantityByDeckId extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            return database.cardDao().getCardQuantityByDeckId(integers[0]);
        }
    }

    public void insertCard(Card card) {
        new InsertCardTask().execute(card);
    }

    private static class InsertCardTask extends AsyncTask<Card, Void, Void> {
        @Override
        protected Void doInBackground(Card... cards) {
            database.cardDao().insetCard(cards[0]);
            return null;
        }
    }

    public void insertCardList(List<Card> cards) {
        new InsetCardListTask().execute(cards);
    }

    private static class InsetCardListTask extends AsyncTask<List<Card>, Void, Void> {
        @Override
        protected Void doInBackground(List<Card>... lists) {
            database.cardDao().insetCardList(lists[0]);
            return null;
        }
    }

    public List<Card> getAllCardsList() {
        List<Card> result = null;
        try {
            result = new GetAllCardsListTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetAllCardsListTask extends AsyncTask<Void, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Void... voids) {
            return database.cardDao().getAllCardsList();
        }
    }

    public List<Card> getCardsByDeckId(int deckId) {
        List<Card> result = null;
        try {
            result = new GetCardsByDeckIdTask().execute(deckId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetCardsByDeckIdTask extends AsyncTask<Integer, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Integer... integers) {
            return database.cardDao().getCardsByDeck(integers[0]);
        }
    }

    public Card getCardById(int id) {
        Card result = null;
        try {
            result = new GetCardByIdTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetCardByIdTask extends AsyncTask<Integer, Void, Card> {
        @Override
        protected Card doInBackground(Integer... integers) {
            return database.cardDao().getCardById(integers[0]);
        }
    }

    public void deleteCard(Card card) {
        new DeleteCardTask().execute(card);
    }

    private static class DeleteCardTask extends AsyncTask<Card, Void, Void> {
        @Override
        protected Void doInBackground(Card... cards) {
            database.cardDao().deleteCard(cards[0]);
            return null;
        }
    }

    public void deleteCardsByDeckId(int id) {
        new DeleteCardsByDeckIdTask().execute(id);
    }

    private static class DeleteCardsByDeckIdTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            database.cardDao().deleteCardsByDeckId(integers[0]);
            return null;
        }
    }

}
