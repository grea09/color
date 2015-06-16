/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yocto.utils;

/**
 *
 * @author antoine
 * @param <T>
 */
public interface Compatible<T extends Compatible> {

    static enum Compatibility {

        COMPATIBLE,
        SATISFIES,
        SATISFIED,
        INCOMPATIBLE;

        public boolean satisfies() {
            return this.equals(SATISFIES) || compatible();
        }

        public boolean compatible() {
            return this.equals(COMPATIBLE);
        }

        public Compatibility and(Compatibility other) {
            return values()[Math.max(ordinal() ,other.ordinal()) ];
        }
    };

    public Compatibility compatible(T other);

}
