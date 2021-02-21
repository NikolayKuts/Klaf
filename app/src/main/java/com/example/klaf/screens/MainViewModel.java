package com.example.klaf.screens;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Delete;

import com.example.klaf.data.KlafDatabase;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private static KlafDatabase database;
    private LiveData<List<Desk>> desks;
    private LiveData<List<Card>> cards;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = KlafDatabase.getInstance(application);
        desks = database.deskDao().getAllDesks();
        cards = database.cardDao().getAllCards();
    }

    public LiveData<List<Desk>> getDesks() {
        return desks;
    }

    public LiveData<List<Card>> getCards() {
        return cards;
    }

    public void insertDesk(Desk desk) {
        new InsertDeskTask().execute(desk);
    }
    private static class InsertDeskTask extends AsyncTask<Desk, Void, Void> {
        @Override
        protected Void doInBackground(Desk... desks) {
            database.deskDao().insetDesk(desks[0]);
            return null;
        }
    }

    public Desk getDeskById(int id) {
        Desk result = null;
        try {
            result = new GetDeskByIdTask().execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetDeskByIdTask extends AsyncTask<Integer, Void, Desk> {
        @Override
        protected Desk doInBackground(Integer... integers) {
            return database.deskDao().getDeskById(integers[0]);
        }
    }

    public List<Desk> getDeskList() {
        List<Desk> result = null;
        try {
            result = new GetDeskListTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static class GetDeskListTask extends AsyncTask<Void, Void, List<Desk>> {
        @Override
        protected List<Desk> doInBackground(Void... voids) {
            return database.deskDao().getDeckList();
        }
    }

    public void removeDesk(Desk desk) {
        new RemoveDeskTask().execute(desk);
    }

    private static class RemoveDeskTask extends AsyncTask<Desk, Void, Void> {
        @Override
        protected Void doInBackground(Desk... desks) {
            database.deskDao().deleteDesk(desks[0]);
            return null;
        }
    }

//    private static class GetDeskListTask extends AsyncTask<Void, Void, List<Desk>> {
//        @Override
//        protected List<Desk> doInBackground(Void... voids) {
//            return database.deskDao().getDeckList();
//        }
//    }

    public List<Integer> getCardQuantityList() {
        List<Integer> result = new ArrayList<>();
        List<Desk> current;
        try {
            current = new GetDeskListTask().execute().get();

            for (Desk desk : current) {
                int quantity = new GetCardQuantityByDeskId().execute(desk.getId()).get();
                result.add(quantity);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getCardQuantityByDeskId(int deskId) {
        int result = 0;
        try {
            result = new GetCardQuantityByDeskId().execute(deskId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetCardQuantityByDeskId extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... integers) {
            return database.cardDao().getCardQuantityByDeskId(integers[0]);
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

    public List<Card> getCardsByDeskId(int deskId) {
        List<Card> result = null;
        try {
            result = new GetCardsByDeskIdTask().execute(deskId).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetCardsByDeskIdTask extends AsyncTask<Integer, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Integer... integers) {
            return database.cardDao().getCardsByDesk(integers[0]);
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

    public void deleteCardsByDeskId(int id) {
        new DeleteCardsByDeskIdTask().execute(id);
    }

    private static class DeleteCardsByDeskIdTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            database.cardDao().deleteCardsByDeskId(integers[0]);
            return null;
        }
    }

}
