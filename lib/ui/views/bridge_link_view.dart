import 'dart:async';

import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/ui/views/bridge_link_viewmodel.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:stacked/stacked.dart';

class BridgeLinkView extends StatefulWidget {
  @override
  State<BridgeLinkView> createState() => _BridgeLinkViewState();
}

class _BridgeLinkViewState extends State<BridgeLinkView> {
  late BridgeLinkViewModel viewModel;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<BridgeLinkViewModel>.reactive(
        viewModelBuilder: () => BridgeLinkViewModel(),
        onViewModelReady: (viewModel) => this.viewModel = viewModel,
        builder: (context, viewModel, _) {
          return Scaffold(
            appBar: AppBar(
              title: Text(S.of(context).linkBridge),
            ),
            body: _buildBody(theme),
            floatingActionButton: _buildFAB(theme),
            floatingActionButtonLocation:
                FloatingActionButtonLocation.centerFloat,
          );
        });
  }

  Widget _buildBody(final ThemeData theme) {
    if (viewModel.hasError) {
      return _buildNoConnection(theme);
    } else if (viewModel.isBusy) {
      return _buildLoading();
    } else if (viewModel.data.isEmpty) {
      return _buildNoResults(theme);
    } else {
      return _buildBridges(theme);
    }
  }

  Widget _buildFAB(final ThemeData theme) {
    final foregroundColor = Colors.white;

    final textStyle =
        theme.textTheme.titleLarge!.copyWith(color: foregroundColor);

    return FloatingActionButton.extended(
      onPressed: () => viewModel.initialise(),
      icon: Icon(Icons.search_outlined, color: foregroundColor),
      label: Text(S.of(context).buttonSearchAgain, style: textStyle),
    );
  }

  Future<void> _buildConnectDialog(
      final ThemeData theme, final BridgeModel bridge) {
    return showDialog<double>(
        context: context,
        builder: (_) {
          return LinkDialog(model: viewModel, bridge: bridge);
        });
  }

  Future<void> _buildDisconnectDialog(
      final ThemeData theme, final BridgeModel bridge) {
    return showDialog<double>(
        context: context,
        builder: (context) {
          return AlertDialog(
              title: Text(S.of(context).unlinkBridgeName(bridge.name!)),
              content: Text(S.of(context).hintTurnsOffLightAction),
              actions: [
                TextButton(
                  onPressed: () => viewModel.cancelDialog(),
                  child: Text(S.of(context).dialogCancel),
                ),
                TextButton(
                  onPressed: () => viewModel.remove(bridge),
                  child: Text(S.of(context).dialogUnlink),
                )
              ]);
        });
  }

  Widget _buildLoading() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircularProgressIndicator(),
          SizedBox(
            height: kVerticalPadding,
          ),
          Text(S.of(context).stateSearching),
        ],
      ),
    );
  }

  Widget _buildNoConnection(final ThemeData theme) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset('assets/img_no_connection.webp', width: 200),
          SizedBox(height: kVerticalPadding),
          Text(S.of(context).errorNoConnection,
              style: theme.textTheme.titleLarge),
          SizedBox(height: kVerticalPaddingBig),
        ],
      ),
    );
  }

  Widget _buildNoResults(final ThemeData theme) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset('assets/img_no_results.webp', width: 200),
          SizedBox(height: kVerticalPadding),
          Text(S.of(context).errorNoDevices, style: theme.textTheme.titleLarge),
          SizedBox(height: kVerticalPaddingBig),
        ],
      ),
    );
  }

  Column _buildBridges(final ThemeData theme) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        SectionHeader(
          S.of(context).countDevicesFound(viewModel.data.length),
          leftPadding: kHorizontalPadding,
        ),
        ListView.separated(
          shrinkWrap: true,
          itemBuilder: (_, int index) {
            final BridgeModel item = viewModel.data[index];

            String connectionHint = '';
            switch (item.state) {
              case Connection.unsaved:
                connectionHint = S.of(context).tapToConnect;
                break;
              case Connection.connected:
                connectionHint = S.of(context).connectionStateConnected;
                break;
              case Connection.failed:
                connectionHint = S.of(context).connectionStateFailed;
                break;
              default:
            }

            return ListTile(
                leading: Icon(Icons.device_hub_outlined),
                title: Text('${item.name}'),
                subtitle: Text(item.ip),
                trailing: Text(
                  connectionHint,
                  textAlign: TextAlign.center,
                ),
                onTap: () {
                  if (item.state != Connection.connected) {
                    _buildConnectDialog(theme, item);
                  }

                  // No unlinking possible for now
                  // _buildDisconnectDialog(theme, item)
                });
          },
          itemCount: viewModel.data.length,
          separatorBuilder: (_, int index) => Divider(),
        ),
      ],
    );
  }
}

class LinkDialog extends StatelessWidget {
  const LinkDialog({
    super.key,
    required this.model,
    required this.bridge,
  });

  final BridgeLinkViewModel? model;
  final BridgeModel bridge;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    Connection? connection;

    return StatefulBuilder(builder: (_, setState) {
      return AlertDialog(
          title: Text('${bridge.name}'),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(S.of(context).linkBridgeInstruction),
                SizedBox(height: kVerticalPadding),
                Image.asset('assets/img_pushlink_bridge.webp',
                    width: 88, color: theme.iconTheme.color),
                if (connection != null)
                  _buildConnectionState(context, theme, connection!),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () {
                model?.cancelDialog();
                setState(() {});
              },
              child: Text(S.of(context).dialogCancel),
            ),
            TextButton(
              onPressed: () async {
                setState(() {
                  connection = Connection.pending;
                });

                final result = await model?.linkBridge(bridge);
                setState(() {
                  connection = result;
                });
              },
              child: Text(S.of(context).dialogConnect),
            )
          ]);
    });
  }
}

Widget _buildConnectionState(
    BuildContext context, ThemeData theme, Connection connection) {
  final status = switch (connection) {
    Connection.failed => S.of(context).linkingFailed,
    Connection.pending => S.of(context).linkingPending,
    Connection.connected => S.of(context).connectionStateConnected,
    _ => S.of(context).linkingUnknownError,
  };

  return Padding(
    padding: const EdgeInsets.only(top: kVerticalPadding),
    child: Row(
      children: [
        Text(S.of(context).linkingState(status),
            style: theme.textTheme.titleMedium!),
        if (connection == Connection.pending)
          Padding(
            padding: const EdgeInsets.only(left: 8),
            child: SizedBox(
              height: 16,
              width: 16,
              child: Center(
                  child: CircularProgressIndicator(
                strokeWidth: 2,
              )),
            ),
          ),
      ],
    ),
  );
}
