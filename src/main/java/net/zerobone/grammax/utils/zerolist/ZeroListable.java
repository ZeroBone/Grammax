package net.zerobone.grammax.utils.zerolist;

public interface ZeroListable {

    /**
     * Gets the id for the current element.
     * @param zeroList the instance that makes storing of single elements in different {@link ZeroList} instances possible.
     * @return the id for the current element.
     */
    int getId();

    /**
     * Sets the id for the current element.
     * @param id the id to be set.
     * @param zeroList the instance that makes storing of single elements in different {@link ZeroList} instances possible.
     */
    void setId(int id);

}