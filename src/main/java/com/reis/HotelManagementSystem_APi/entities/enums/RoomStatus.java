package com.reis.HotelManagementSystem_APi.entities.enums;

public enum RoomStatus {

    DISPONIVEL(1, "Quarto disponivel"),
    OCUPADO(2, "Quarto ocupado"),
    MANUTENCAO(3, "Quarto em manutenção"),
    LIMPEZA(4, "Quarto em limpeza");
    
    private final int id;
    private final String description;

    RoomStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static RoomStatus valueOf(int id) {
        for (RoomStatus value : RoomStatus.values()) {
            if (value.getId() == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("ID de Status inválido: " + id);
    }
}