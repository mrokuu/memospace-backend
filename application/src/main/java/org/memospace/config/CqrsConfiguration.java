package org.memospace.config;

import lombok.RequiredArgsConstructor;
import org.memospace.service.command.card.CreateCardCommand;
import org.memospace.service.command.card.DeleteCardCommand;
import org.memospace.service.command.card.ReviewCardCommand;
import org.memospace.service.command.card.UpdateCardCommand;
import org.memospace.service.command.note.CreateNoteCommand;
import org.memospace.service.command.note.DeleteNoteCommand;
import org.memospace.service.command.note.RegenerateCardsCommand;
import org.memospace.service.command.note.UpdateNoteCommand;
import org.memospace.service.command.notetype.CreateNoteTypeCommand;
import org.memospace.service.command.notetype.DeleteNoteTypeCommand;
import org.memospace.service.command.notetype.UpdateNoteTypeCommand;
import org.memospace.service.command.deck.CreateDeckCommand;
import org.memospace.service.command.deck.DeleteDeckCommand;
import org.memospace.service.command.deck.UpdateDeckCommand;
import org.memospace.service.command.filtereddeck.CreateFilteredDeckCommand;
import org.memospace.service.command.filtereddeck.RebuildFilteredDeckCommand;
import org.memospace.service.command.filtereddeck.DeleteFilteredDeckCommand;
import org.memospace.service.command.importexport.ImportDataCommand;
import org.memospace.service.command.media.UploadMediaCommand;
import org.memospace.service.handler.card.CreateCardCommandHandler;
import org.memospace.service.handler.card.DeleteCardCommandHandler;
import org.memospace.service.handler.card.GetCardQueryHandler;
import org.memospace.service.handler.card.GetNextDueCardsQueryHandler;
import org.memospace.service.handler.card.ReviewCardCommandHandler;
import org.memospace.service.handler.card.SearchCardsQueryHandler;
import org.memospace.service.handler.card.UpdateCardCommandHandler;
import org.memospace.service.handler.note.CreateNoteCommandHandler;
import org.memospace.service.handler.note.DeleteNoteCommandHandler;
import org.memospace.service.handler.note.GetNoteCardsQueryHandler;
import org.memospace.service.handler.note.GetNoteQueryHandler;
import org.memospace.service.handler.note.RegenerateCardsCommandHandler;
import org.memospace.service.handler.note.SearchNotesQueryHandler;
import org.memospace.service.handler.note.UpdateNoteCommandHandler;
import org.memospace.service.handler.notetype.CreateNoteTypeCommandHandler;
import org.memospace.service.handler.notetype.DeleteNoteTypeCommandHandler;
import org.memospace.service.handler.notetype.GetAllNoteTypesQueryHandler;
import org.memospace.service.handler.notetype.GetNoteTypeQueryHandler;
import org.memospace.service.handler.notetype.UpdateNoteTypeCommandHandler;
import org.memospace.service.handler.review.GetReviewHistoryQueryHandler;
import org.memospace.service.handler.deck.CreateDeckCommandHandler;
import org.memospace.service.handler.deck.DeleteDeckCommandHandler;
import org.memospace.service.handler.deck.GetDeckQueryHandler;
import org.memospace.service.handler.deck.ListDecksQueryHandler;
import org.memospace.service.handler.deck.UpdateDeckCommandHandler;
import org.memospace.service.handler.filtereddeck.CreateFilteredDeckCommandHandler;
import org.memospace.service.handler.filtereddeck.RebuildFilteredDeckCommandHandler;
import org.memospace.service.handler.filtereddeck.DeleteFilteredDeckCommandHandler;
import org.memospace.service.handler.filtereddeck.GetFilteredDeckQueryHandler;
import org.memospace.service.handler.filtereddeck.GetNextForFilteredDeckQueryHandler;
import org.memospace.service.handler.export.ExportAllQueryHandler;
import org.memospace.service.handler.export.ExportDeckQueryHandler;
import org.memospace.service.handler.importexport.ImportDataCommandHandler;
import org.memospace.service.handler.media.DiagnoseMediaQueryHandler;
import org.memospace.service.handler.media.StreamMediaQueryHandler;
import org.memospace.service.handler.media.UploadMediaCommandHandler;
import org.memospace.service.handler.stats.GetStatsHeatmapQueryHandler;
import org.memospace.service.handler.stats.GetStatsHistogramsQueryHandler;
import org.memospace.service.handler.stats.GetStatsOverviewQueryHandler;
import org.memospace.service.impl.SimpleCommandBus;
import org.memospace.service.impl.SimpleQueryBus;
import org.memospace.service.query.card.GetCardQuery;
import org.memospace.service.query.card.GetNextDueCardsQuery;
import org.memospace.service.query.card.SearchCardsQuery;
import org.memospace.service.query.note.GetNoteCardsQuery;
import org.memospace.service.query.note.GetNoteQuery;
import org.memospace.service.query.note.SearchNotesQuery;
import org.memospace.service.query.notetype.GetAllNoteTypesQuery;
import org.memospace.service.query.notetype.GetNoteTypeQuery;
import org.memospace.service.query.review.GetReviewHistoryQuery;
import org.memospace.service.query.deck.GetDeckQuery;
import org.memospace.service.query.deck.ListDecksQuery;
import org.memospace.service.query.filtereddeck.GetFilteredDeckQuery;
import org.memospace.service.query.filtereddeck.GetNextForFilteredDeckQuery;
import org.memospace.service.query.export.ExportAllQuery;
import org.memospace.service.query.export.ExportDeckQuery;
import org.memospace.service.query.media.DiagnoseMediaQuery;
import org.memospace.service.query.media.StreamMediaQuery;
import org.memospace.service.query.stats.GetStatsHeatmapQuery;
import org.memospace.service.query.stats.GetStatsHistogramsQuery;
import org.memospace.service.query.stats.GetStatsOverviewQuery;
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