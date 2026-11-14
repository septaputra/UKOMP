package com.example.studymate.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardViewModelFilterTest {
    private val viewModel = DashboardViewModel()

    @Test
    fun learningVideoSearchAndFiltersAreCombined() {
        val result = viewModel.filterLearningVideos(
            query = "Integral",
            educationLevel = "SMA",
            subject = "Matematika"
        )

        assertEquals(listOf(1), result.map { it.id })
        assertEquals(
            emptyList<Int>(),
            viewModel.filterLearningVideos("Integral", "SMA", "Fisika dan Kimia").map { it.id }
        )
    }

    @Test
    fun learningVideoLevelMatchesCombinedLevelValues() {
        val result = viewModel.filterLearningVideos(
            query = "",
            educationLevel = "SMP",
            subject = "Bahasa Inggris"
        )

        assertEquals(listOf(4), result.map { it.id })
    }

    @Test
    fun tutoringSearchLocationAndSubjectAreCombined() {
        val result = viewModel.filterTutoringCenters(
            query = "Ruang",
            location = "Jakarta Selatan (contoh)",
            subject = "Matematika"
        )

        assertEquals(listOf(1), result.map { it.id })
        assertEquals(
            emptyList<Int>(),
            viewModel.filterTutoringCenters("Ruang", "Bandung (contoh)", "Matematika").map { it.id }
        )
    }

    @Test
    fun universalSearchIncludesAllVideosForMaterialAlias() {
        val result = viewModel.searchContent("  MATERI   ")

        assertEquals(
            viewModel.learningVideos.map { it.id },
            result.filter { it.type == DashboardResultType.Video }.map { it.id }
        )
    }

    @Test
    fun universalSearchFindsMultipleContentTypesAndBannerDestinations() {
        val mathResults = viewModel.searchContent("matematika")
        val cityResults = viewModel.searchContent("jakarta selatan")
        val newsResults = viewModel.searchContent("berita")
        val bannerResults = viewModel.searchContent("tips belajar")
        val featureResults = viewModel.searchContent("progress belajar")

        assertEquals(listOf(1), mathResults.filter { it.type == DashboardResultType.Video }.map { it.id })
        assertEquals(listOf(1), mathResults.filter { it.type == DashboardResultType.Tutoring }.map { it.id })
        assertEquals(listOf(1), cityResults.filter { it.type == DashboardResultType.Tutoring }.map { it.id })
        assertEquals(viewModel.news.map { it.id }, newsResults.filter { it.type == DashboardResultType.News }.map { it.id })
        assertEquals("news_detail/1", bannerResults.single { it.type == DashboardResultType.Banner }.destination)
        assertEquals("progress", featureResults.single { it.type == DashboardResultType.Feature }.destination)
    }

    @Test
    fun newsSearchMatchesCategoryTitleAndDescriptionWithAllQueryTerms() {
        assertEquals(
            listOf(2),
            viewModel.filterNews("  PERGURUAN   tinggi ").map { it.id }
        )
        assertEquals(
            listOf(1),
            viewModel.filterNews("target kecil").map { it.id }
        )
        assertEquals(viewModel.news, viewModel.filterNews("  "))
        assertEquals(emptyList<Int>(), viewModel.filterNews("tidak ada berita ini").map { it.id })
    }
}
