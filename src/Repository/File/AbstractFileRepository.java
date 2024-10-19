package Repository.File;

import Domain.Entity;
import Domain.Utilizator;
import Domain.Validators.Validator;
import Repository.Repository;
import Repository.Memory.InMemoryRepository;

import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E>{
    private String filename;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        filename=fileName;
        loadData();
    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                E e = createEntity(linie);
                super.save(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract E createEntity(String line);
    public abstract String saveEntity(E entity);

    @Override
    public E findOne(ID id) {
        return super.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        return super.findAll();
    }

    @Override
    public E save(E entity) {
        E e = super.save(entity);
        if (e == null)
            writeToFile();
        return e;
    }

    private void writeToFile() {

        try  ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            for (E entity: entities.values()) {
                String ent = saveEntity(entity);
                writer.write(ent);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public E delete(ID id) {
        E deletedEntity = super.delete(id); // Use the inherited method from InMemoryRepository
        if (deletedEntity != null) {
            writeToFile(); // Update the file after deletion
        }
        return deletedEntity;
    }

    @Override
    public E update(E entity) {
        E updatedEntity = super.update(entity); // Use the inherited method from InMemoryRepository
        if (updatedEntity == null) {
            writeToFile(); // Update the file after the update
        }
        return updatedEntity;
    }
}

