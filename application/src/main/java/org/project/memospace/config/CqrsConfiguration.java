package org.project.memospace.application.service.config;

import lombok.RequiredArgsConstructor;
import org.project.memospace.application.service.command.card.CreateCardCommand;
import org.project.memospace.application.service.command.card.DeleteCardCommand;
import org.project.memospace.application.service.command.card.ReviewCardCommand;
import org.project.memospace.application.service.command.card.UpdateCardCommand;
import org.project.memospace.application.service.command.note.CreateNoteCommand;
import org.project.memospace.application.service.command.note.DeleteNoteCommand;
import org.project.memospace.application.service.command.note.RegenerateCardsCommand;
import org.project.memospace.application.service.command.note.UpdateNoteCommand;
import org.project.memospace.application.service.command.notetype.CreateNoteTypeCommand;
import org.project.memospace.application.service.command.notetype.DeleteNoteTypeCommand;
import org.project.memospace.application.service.command.notetype.UpdateNoteTypeCommand;
import org.project.memospace.application.service.command.deck.CreateDeckCommand;
import org.project.memospace.application.service.command.deck.DeleteDeckCommand;
import org.project.memospace.application.service.command.deck.UpdateDeckCommand;
import org.project.memospace.application.service.command.filtereddeck.CreateFilteredDeckCommand;
import org.project.memospace.application.service.command.filtereddeck.RebuildFilteredDeckCommand;
import org.project.memospace.application.service.command.filtereddeck.DeleteFilteredDeckCommand;
import org.project.memospace.application.service.command.importexport.ImportDataCommand;
import org.project.memospace.application.service.command.media.UploadMediaCommand;
import org.project.memospace.application.service.handler.card.CreateCardCommandHandler;
import org.project.memospace.application.service.handler.card.DeleteCardCommandHandler;
import org.project.memospace.application.service.handler.card.GetCardQueryHandler;
import org.project.memospace.application.service.handler.card.GetNextDueCardsQueryHandler;
import org.project.memospace.application.service.handler.card.ReviewCardCommandHandler;
import org.project.memospace.application.service.handler.card.SearchCardsQueryHandler;
import org.project.memospace.application.service.handler.card.UpdateCardCommandHandler;
import org.project.memospace.application.service.handler.note.CreateNoteCommandHandler;
import org.project.memospace.application.service.handler.note.DeleteNoteCommandHandler;
import org.project.memospace.application.service.handler.note.GetNoteCardsQueryHandler;
import org.project.memospace.application.service.handler.note.GetNoteQueryHandler;
import org.project.memospace.application.service.handler.note.RegenerateCardsCommandHandler;
import org.project.memospace.application.service.handler.note.SearchNotesQueryHandler;
import org.project.memospace.application.service.handler.note.UpdateNoteCommandHandler;
import org.project.memospace.application.service.handler.notetype.CreateNoteTypeCommandHandler;
import org.project.memospace.application.service.handler.notetype.DeleteNoteTypeCommandHandler;
import org.project.memospace.application.service.handler.notetype.GetAllNoteTypesQueryHandler;
import org.project.memospace.application.service.handler.notetype.GetNoteTypeQueryHandler;
import org.project.memospace.application.service.handler.notetype.UpdateNoteTypeCommandHandler;
import org.project.memospace.application.service.handler.review.GetReviewHistoryQueryHandler;
import org.project.memospace.application.service.handler.deck.CreateDeckCommandHandler;
import org.project.memospace.application.service.handler.deck.DeleteDeckCommandHandler;
import org.project.memospace.application.service.handler.deck.GetDeckQueryHandler;
import org.project.memospace.application.service.handler.deck.ListDecksQueryHandler;
import org.project.memospace.application.service.handler.deck.UpdateDeckCommandHandler;
import org.project.memospace.application.service.handler.filtereddeck.CreateFilteredDeckCommandHandler;
import org.project.memospace.application.service.handler.filtereddeck.RebuildFilteredDeckCommandHandler;
import org.project.memospace.application.service.handler.filtereddeck.DeleteFilteredDeckCommandHandler;
import org.project.memospace.application.service.handler.filtereddeck.GetFilteredDeckQueryHandler;
import org.project.memospace.application.service.handler.filtereddeck.GetNextForFilteredDeckQueryHandler;
import org.project.memospace.application.service.handler.export.ExportAllQueryHandler;
import org.project.memospace.application.service.handler.export.ExportDeckQueryHandler;
import org.project.memospace.application.service.handler.importexport.ImportDataCommandHandler;
import org.project.memospace.application.service.handler.media.DiagnoseMediaQueryHandler;
import org.project.memospace.application.service.handler.media.StreamMediaQueryHandler;
import org.project.memospace.application.service.handler.media.UploadMediaCommandHandler;
import org.project.memospace.application.service.handler.stats.GetStatsHeatmapQueryHandler;
import org.project.memospace.application.service.handler.stats.GetStatsHistogramsQueryHandler;
import org.project.memospace.application.service.handler.stats.GetStatsOverviewQueryHandler;
import org.project.memospace.application.service.impl.SimpleCommandBus;
import org.project.memospace.application.service.impl.SimpleQueryBus;
import org.project.memospace.application.service.query.card.GetCardQuery;
import org.project.memospace.application.service.query.card.GetNextDueCardsQuery;
import org.project.memospace.application.service.query.card.SearchCardsQuery;
import org.project.memospace.application.service.query.note.GetNoteCardsQuery;
import org.project.memospace.application.service.query.note.GetNoteQuery;
import org.project.memospace.application.service.query.note.SearchNotesQuery;
import org.project.memospace.application.service.query.notetype.GetAllNoteTypesQuery;
import org.project.memospace.application.service.query.notetype.GetNoteTypeQuery;
import org.project.memospace.application.service.query.review.GetReviewHistoryQuery;
import org.project.memospace.application.service.query.deck.GetDeckQuery;
import org.project.memospace.application.service.query.deck.ListDecksQuery;
import org.project.memospace.application.service.query.filtereddeck.GetFilteredDeckQuery;
import org.project.memospace.application.service.query.filtereddeck.GetNextForFilteredDeckQuery;
import org.project.memospace.application.service.query.export.ExportAllQuery;
import org.project.memospace.application.service.query.export.ExportDeckQuery;
import org.project.memospace.application.service.query.media.DiagnoseMediaQuery;
import org.project.memospace.application.service.query.media.StreamMediaQuery;
import org.project.memospace.application.service.query.stats.GetStatsHeatmapQuery;
import org.project.memospace.application.service.query.stats.GetStatsHistogramsQuery;
import org.project.memospace.application.service.query.stats.GetStatsOverviewQuery;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class CqrsConfiguration {

    private final SimpleCommandBus commandBus;
    private final SimpleQueryBus queryBus;

    // Command handlers
    private final CreateDeckCommandHandler createDeckCommandHandler;
    private final UpdateDeckCommandHandler updateDeckCommandHandler;
    private final DeleteDeckCommandHandler deleteDeckCommandHandler;
    private final CreateCardCommandHandler createCardCommandHandler;
    private final UpdateCardCommandHandler updateCardCommandHandler;
    private final DeleteCardCommandHandler deleteCardCommandHandler;
    private final ReviewCardCommandHandler reviewCardCommandHandler;
    private final CreateNoteCommandHandler createNoteCommandHandler;
    private final UpdateNoteCommandHandler updateNoteCommandHandler;
    private final DeleteNoteCommandHandler deleteNoteCommandHandler;
    private final RegenerateCardsCommandHandler regenerateCardsCommandHandler;
    private final CreateNoteTypeCommandHandler createNoteTypeCommandHandler;
    private final UpdateNoteTypeCommandHandler updateNoteTypeCommandHandler;
    private final DeleteNoteTypeCommandHandler deleteNoteTypeCommandHandler;
    private final CreateFilteredDeckCommandHandler createFilteredDeckCommandHandler;
    private final RebuildFilteredDeckCommandHandler rebuildFilteredDeckCommandHandler;
    private final DeleteFilteredDeckCommandHandler deleteFilteredDeckCommandHandler;
    private final ImportDataCommandHandler importDataCommandHandler;
    private final UploadMediaCommandHandler uploadMediaCommandHandler;

    // Query handlers
    private final GetDeckQueryHandler getDeckQueryHandler;
    private final ListDecksQueryHandler listDecksQueryHandler;
    private final GetCardQueryHandler getCardQueryHandler;
    private final SearchCardsQueryHandler searchCardsQueryHandler;
    private final GetNextDueCardsQueryHandler getNextDueCardsQueryHandler;
    private final GetNoteQueryHandler getNoteQueryHandler;
    private final SearchNotesQueryHandler searchNotesQueryHandler;
    private final GetNoteCardsQueryHandler getNoteCardsQueryHandler;
    private final GetNoteTypeQueryHandler getNoteTypeQueryHandler;
    private final GetAllNoteTypesQueryHandler getAllNoteTypesQueryHandler;
    private final GetReviewHistoryQueryHandler getReviewHistoryQueryHandler;
    private final GetFilteredDeckQueryHandler getFilteredDeckQueryHandler;
    private final GetNextForFilteredDeckQueryHandler getNextForFilteredDeckQueryHandler;
    private final ExportAllQueryHandler exportAllQueryHandler;
    private final ExportDeckQueryHandler exportDeckQueryHandler;
    private final StreamMediaQueryHandler streamMediaQueryHandler;
    private final DiagnoseMediaQueryHandler diagnoseMediaQueryHandler;
    private final GetStatsOverviewQueryHandler getStatsOverviewQueryHandler;
    private final GetStatsHeatmapQueryHandler getStatsHeatmapQueryHandler;
    private final GetStatsHistogramsQueryHandler getStatsHistogramsQueryHandler;

    @PostConstruct
    public void registerHandlers() {
        // Register command handlers
        commandBus.registerHandler(CreateDeckCommand.class, createDeckCommandHandler);
        commandBus.registerHandler(UpdateDeckCommand.class, updateDeckCommandHandler);
        commandBus.registerHandler(DeleteDeckCommand.class, deleteDeckCommandHandler);
        commandBus.registerHandler(CreateCardCommand.class, createCardCommandHandler);
        commandBus.registerHandler(UpdateCardCommand.class, updateCardCommandHandler);
        commandBus.registerHandler(DeleteCardCommand.class, deleteCardCommandHandler);
        commandBus.registerHandler(ReviewCardCommand.class, reviewCardCommandHandler);
        commandBus.registerHandler(CreateNoteCommand.class, createNoteCommandHandler);
        commandBus.registerHandler(UpdateNoteCommand.class, updateNoteCommandHandler);
        commandBus.registerHandler(DeleteNoteCommand.class, deleteNoteCommandHandler);
        commandBus.registerHandler(RegenerateCardsCommand.class, regenerateCardsCommandHandler);
        commandBus.registerHandler(CreateNoteTypeCommand.class, createNoteTypeCommandHandler);
        commandBus.registerHandler(UpdateNoteTypeCommand.class, updateNoteTypeCommandHandler);
        commandBus.registerHandler(DeleteNoteTypeCommand.class, deleteNoteTypeCommandHandler);
        commandBus.registerHandler(CreateFilteredDeckCommand.class, createFilteredDeckCommandHandler);
        commandBus.registerHandler(RebuildFilteredDeckCommand.class, rebuildFilteredDeckCommandHandler);
        commandBus.registerHandler(DeleteFilteredDeckCommand.class, deleteFilteredDeckCommandHandler);
        commandBus.registerHandler(ImportDataCommand.class, importDataCommandHandler);
        commandBus.registerHandler(UploadMediaCommand.class, uploadMediaCommandHandler);

        // Register query handlers
        queryBus.registerHandler(GetDeckQuery.class, getDeckQueryHandler);
        queryBus.registerHandler(ListDecksQuery.class, listDecksQueryHandler);
        queryBus.registerHandler(GetCardQuery.class, getCardQueryHandler);
        queryBus.registerHandler(SearchCardsQuery.class, searchCardsQueryHandler);
        queryBus.registerHandler(GetNextDueCardsQuery.class, getNextDueCardsQueryHandler);
        queryBus.registerHandler(GetNoteQuery.class, getNoteQueryHandler);
        queryBus.registerHandler(SearchNotesQuery.class, searchNotesQueryHandler);
        queryBus.registerHandler(GetNoteCardsQuery.class, getNoteCardsQueryHandler);
        queryBus.registerHandler(GetNoteTypeQuery.class, getNoteTypeQueryHandler);
        queryBus.registerHandler(GetAllNoteTypesQuery.class, getAllNoteTypesQueryHandler);
        queryBus.registerHandler(GetReviewHistoryQuery.class, getReviewHistoryQueryHandler);
        queryBus.registerHandler(GetFilteredDeckQuery.class, getFilteredDeckQueryHandler);
        queryBus.registerHandler(GetNextForFilteredDeckQuery.class, getNextForFilteredDeckQueryHandler);
        queryBus.registerHandler(ExportAllQuery.class, exportAllQueryHandler);
        queryBus.registerHandler(ExportDeckQuery.class, exportDeckQueryHandler);
        queryBus.registerHandler(StreamMediaQuery.class, streamMediaQueryHandler);
        queryBus.registerHandler(DiagnoseMediaQuery.class, diagnoseMediaQueryHandler);
        queryBus.registerHandler(GetStatsOverviewQuery.class, getStatsOverviewQueryHandler);
        queryBus.registerHandler(GetStatsHeatmapQuery.class, getStatsHeatmapQueryHandler);
        queryBus.registerHandler(GetStatsHistogramsQuery.class, getStatsHistogramsQueryHandler);
    }
}