package org.ensime

import java.io.File

import org.ensime.core._
import org.ensime.model._
import org.ensime.server.ConnectionInfo
import org.ensime.util.FileRange

trait EnsimeApi {

  def rpcConnectionInfo(): ConnectionInfo
  def rpcShutdownServer(): Unit

  /**
   * Subscribe to async events from the project, replaying previously seen events if requested.
   * The first subscriber will get all undelivered events (subsequent subscribers do not).
   * @param handler The callback handler for events
   * @return True if caller is first subscriber, False otherwise
   */
  def rpcSubscribeAsync(handler: EnsimeEvent => Unit): Boolean

  /**
   * Return the details of the latest Undo operation on the undo stack.
   * @return The latest Undo information (if it exists) or None
   */
  def rpcPeekUndo(): Option[Undo]

  def rpcExecUndo(undoId: Int): Either[String, UndoResult]

  def rpcReplConfig(): ReplConfig

  /**
   *   Request the semantic classes of symbols in the given range. These classes are intended to be used for
   *   semantic highlighting.
   * Arguments:
   *   f source filename
   *   start The character offset of the start of the input range.
   *   End  The character offset of the end of the input range.
   *   requestedTypes The semantic classes in which we are interested. (@see SourceSymbol)
   * Return:
   *   SymbolDesignations The given
   */
  def rpcSymbolDesignations(f: File, start: Int, end: Int, requestedTypes: List[SourceSymbol]): SymbolDesignations

  /**
   *   Patch the source with the given changes.
   *   @param f The file to patch
   *   @param edits The patches to apply to the file.
   */
  def rpcPatchSource(f: File, edits: List[PatchOp]): Unit

  def rpcTypecheckFile(fileInfo: SourceFileInfo): Unit
  def rpcTypecheckFiles(fs: List[File]): Unit
  def rpcRemoveFile(f: File): Unit
  def rpcUnloadAll(): Unit
  def rpcTypecheckAll(): Unit
  def rpcCompletionsAtPoint(fileInfo: SourceFileInfo, point: Int, maxResults: Int, caseSens: Boolean, reload: Boolean): CompletionInfoList
  def rpcPackageMemberCompletion(path: String, prefix: String): List[CompletionInfo]

  /**
   * Return detailed type information about the item at the given file position.
   * @param fileName The source filename
   * @param range The range in the file to inspect.
   * @return Some(TypeInspectInfo) if the range represents a valid type, None otherwise
   */
  def rpcInspectTypeAtPoint(fileName: File, range: OffsetRange): Option[TypeInspectInfo]

  /**
   * Lookup detailed type description by typeId
   * @param typeId The id of the type to inspect (returned by other calls)
   * @return Some(TypeInspectInfo) if the typeId represents a valid type, None otherwise
   */
  def rpcInspectTypeById(typeId: Int): Option[TypeInspectInfo]

  /**
   * Lookup detailed type description by fully qualified class name
   * @param typeFQN The fully qualified type name to inspect
   * @return Some(TypeInspectInfo) if typeFQN represents a valid type, None otherwise
   */
  def rpcInspectTypeByName(typeFQN: String): Option[TypeInspectInfo]

  def rpcSymbolAtPoint(fileName: File, point: Int): Option[SymbolInfo]

  /**
   * Lookup a detailed symbol description.
   * @param fullyQualifiedName The fully qualified name of a type, object or package.
   * @param memberName The short name of a member symbol of the qualified symbol.
   * @return signatureString An optional signature to disambiguate overloaded methods.
   */
  def rpcSymbolByName(fullyQualifiedName: String, memberName: Option[String], signatureString: Option[String]): Option[SymbolInfo]
  def rpcTypeById(id: Int): Option[TypeInfo]
  def rpcTypeByName(name: String): Option[TypeInfo]
  def rpcTypeByNameAtPoint(name: String, f: File, range: OffsetRange): Option[TypeInfo]
  def rpcCallCompletion(id: Int): Option[CallCompletionInfo]
  def rpcImportSuggestions(f: File, point: Int, names: List[String], maxResults: Int): ImportSuggestions
  def rpcDocSignatureAtPoint(f: File, point: OffsetRange): Option[DocSigPair]
  def rpcDocSignatureForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Option[DocSigPair]
  def rpcDocUriAtPoint(f: File, point: OffsetRange): Option[String]
  def rpcDocUriForSymbol(typeFullName: String, memberName: Option[String], signatureString: Option[String]): Option[String]
  def rpcPublicSymbolSearch(names: List[String], maxResults: Int): SymbolSearchResults
  def rpcUsesOfSymAtPoint(f: File, point: Int): List[ERangePosition]
  def rpcTypeAtPoint(f: File, range: OffsetRange): Option[TypeInfo]
  def rpcInspectPackageByPath(path: String): Option[PackageInfo]

  def rpcPrepareRefactor(procId: Int, refactorDesc: RefactorDesc): Either[RefactorFailure, RefactorEffect]
  def rpcExecRefactor(procId: Int, refactorType: Symbol): Either[RefactorFailure, RefactorResult]
  def rpcCancelRefactor(procId: Int): Unit

  def rpcExpandSelection(filename: File, start: Int, stop: Int): FileRange
  def rpcFormatFiles(filenames: List[File]): Unit
  def rpcFormatFile(fileInfo: SourceFileInfo): String

  def rpcDebugStartVM(commandLine: String): DebugVmStatus
  def rpcDebugAttachVM(hostname: String, port: String): DebugVmStatus
  def rpcDebugStopVM(): Boolean
  def rpcDebugRun(): Boolean
  def rpcDebugContinue(threadId: DebugThreadId): Boolean
  def rpcDebugSetBreakpoint(file: File, line: Int): Unit
  def rpcDebugClearBreakpoint(file: File, line: Int): Unit
  def rpcDebugClearAllBreakpoints(): Unit
  def rpcDebugListBreakpoints(): BreakpointList
  def rpcDebugNext(threadId: DebugThreadId): Boolean
  def rpcDebugStep(threadId: DebugThreadId): Boolean
  def rpcDebugStepOut(threadId: DebugThreadId): Boolean
  def rpcDebugLocateName(threadId: DebugThreadId, name: String): Option[DebugLocation]
  def rpcDebugValue(loc: DebugLocation): Option[DebugValue]
  def rpcDebugToString(threadId: DebugThreadId, loc: DebugLocation): Option[String]
  def rpcDebugSetValue(loc: DebugLocation, newValue: String): Boolean
  def rpcDebugBacktrace(threadId: DebugThreadId, index: Int, count: Int): DebugBacktrace
  def rpcDebugActiveVM(): Boolean
}

