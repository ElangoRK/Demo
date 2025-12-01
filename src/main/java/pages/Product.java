package pages;

import java.util.Objects;

/**
 * Normalization rules:
 *  - name and price are trimmed (null -> empty string)
 *  - equality is exact on the trimmed strings (keeps feature table exact-match behavior)
 */
public final class Product {
    private final String name;
    private final String price;

    public Product(String name, String price) {
        this.name = normalize(name);
        this.price = normalize(price);
    }

    public static Product from(String name, String price) {
        return new Product(name, price);
    }

    private static String normalize(String s) {
        return (s == null) ? "" : s.trim();
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("Product{name='%s', price='%s'}", name, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return Objects.equals(this.name, other.name) &&
                Objects.equals(this.price, other.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
