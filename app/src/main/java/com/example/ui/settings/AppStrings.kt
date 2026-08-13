package com.example.ui.settings

object AppStrings {
    fun tr(isPt: Boolean, pt: String, en: String): String {
        return if (isPt) pt else en
    }

    // Tabs
    fun character(isPt: Boolean) = tr(isPt, "Personagem", "Character")
    fun spells(isPt: Boolean) = tr(isPt, "Magias", "Spells")
    fun inventory(isPt: Boolean) = tr(isPt, "Inventário", "Inventory")
    fun combat(isPt: Boolean) = tr(isPt, "Combate", "Combat")
    fun notes(isPt: Boolean) = tr(isPt, "Notas", "Notes")

    // Actions & Headers
    fun settings(isPt: Boolean) = tr(isPt, "Configurações", "Settings")
    fun ascension(isPt: Boolean) = tr(isPt, "Ascensão de Nível", "Level Up")
    fun rest(isPt: Boolean) = tr(isPt, "Descanso", "Rest")
    fun importSheet(isPt: Boolean) = tr(isPt, "Importar Ficha", "Import Sheet")
    fun save(isPt: Boolean) = tr(isPt, "Salvar", "Save")
    fun cancel(isPt: Boolean) = tr(isPt, "Cancelar", "Cancel")
    fun close(isPt: Boolean) = tr(isPt, "Fechar", "Close")
    fun selectLanguage(isPt: Boolean) = tr(isPt, "Idioma do Aplicativo", "App Language")
    fun rulesLibrary(isPt: Boolean) = tr(isPt, "Acervo de Regras (Livros & Suplementos)", "Rules Library (Sourcebooks)")
    fun editCharacter(isPt: Boolean) = tr(isPt, "Editar Ficha", "Edit Character")
    fun characterClass(isPt: Boolean) = tr(isPt, "Classe", "Class")
    fun subclass(isPt: Boolean) = tr(isPt, "Subclasse", "Subclass")
    fun race(isPt: Boolean) = tr(isPt, "Raça", "Race")
    fun background(isPt: Boolean) = tr(isPt, "Antecedente", "Background")
    fun level(isPt: Boolean) = tr(isPt, "Nível", "Level")
    fun hp(isPt: Boolean) = tr(isPt, "Pontos de Vida (PV)", "Hit Points (HP)")
    fun ac(isPt: Boolean) = tr(isPt, "Classe de Armadura (CA)", "Armor Class (AC)")
    fun speed(isPt: Boolean) = tr(isPt, "Deslocamento", "Speed")
}
