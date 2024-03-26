package com.uniboard.backend.domain

interface StorageDB {
    fun add(boardId: Long): Long

    fun fileExists(boardId: Long, id: Long): Boolean

    fun delete(boardId: Long, id: Long)
}
