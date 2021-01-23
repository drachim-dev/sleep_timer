import 'package:flutter/material.dart';
import 'package:sleep_timer/common/constants.dart';
import 'package:sleep_timer/generated/l10n.dart';
import 'package:sleep_timer/model/bridge_model.dart';
import 'package:sleep_timer/ui/views/bridge_link_viewmodel.dart';
import 'package:sleep_timer/ui/widgets/section_header.dart';
import 'package:stacked/stacked.dart';

class BridgeLinkView extends StatefulWidget {
  @override
  _BridgeLinkViewState createState() => _BridgeLinkViewState();
}

class _BridgeLinkViewState extends State<BridgeLinkView> {
  BridgeLinkViewModel model;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ViewModelBuilder<BridgeLinkViewModel>.reactive(
        viewModelBuilder: () => BridgeLinkViewModel(),
        onModelReady: (model) => this.model = model,
        builder: (context, model, child) {
          return Scaffold(
            appBar: AppBar(title: Text(S.of(context).linkBridge)),
            body: _buildBody(theme),
            floatingActionButton: _buildFAB(theme),
            floatingActionButtonLocation:
                FloatingActionButtonLocation.centerFloat,
          );
        });
  }

  Widget _buildBody(final ThemeData theme) {
    if (model.hasError) {
      return _buildNoConnection(theme);
    } else if (model.isBusy) {
      return _buildLoading();
    } else if (model.data.isEmpty) {
      return _buildNoResults(theme);
    } else {
      return _buildBridges(theme);
    }
  }

  Widget _buildFAB(final ThemeData theme) {
    final foregroundColor = Colors.white;

    final textStyle =
        theme.accentTextTheme.headline6.copyWith(color: foregroundColor);

    return FloatingActionButton.extended(
      onPressed: () => model.initialise(),
      icon: Icon(Icons.search_outlined, color: foregroundColor),
      label: Text(S.of(context).buttonSearchAgain, style: textStyle),
    );
  }

  Future<void> _buildConnectDialog(
      final ThemeData theme, final BridgeModel bridge) {
    return showDialog<double>(
        context: context,
        builder: (_) => LinkDialog(model: model, bridge: bridge));
  }

  Future<void> _buildDisconnectDialog(
      final ThemeData theme, final BridgeModel bridge) {
    return showDialog<double>(
        context: context,
        builder: (context) {
          return AlertDialog(
              title: Text(S.of(context).unlinkBridgeName(bridge.name)),
              content: Text(S.of(context).hintTurnsOffLightAction),
              actions: [
                FlatButton(
                  onPressed: () => model.cancelDialog(),
                  child: Text(S.of(context).dialogCancel),
                ),
                FlatButton(
                  onPressed: () => model.remove(bridge),
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
              style: theme.textTheme.headline6),
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
          Text(S.of(context).errorNoDevices, style: theme.textTheme.headline6),
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
          S.of(context).countDevicesFound(model.data.length),
          leftPadding: kPreferenceTitleLeftPadding,
        ),
        ListView.separated(
          shrinkWrap: true,
          itemBuilder: (_, int index) {
            final BridgeModel item = model.data[index];

            var connectionHint;
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
                title: Text('${item.name} '),
                subtitle: Text('${item.ip}'),
                trailing: Text(
                  connectionHint,
                  textAlign: TextAlign.center,
                ),
                onTap: () => item.state == Connection.connected
                    ? _buildDisconnectDialog(theme, item)
                    : _buildConnectDialog(theme, item));
          },
          itemCount: model.data.length,
          separatorBuilder: (_, int index) => Divider(),
        ),
      ],
    );
  }
}

class LinkDialog extends StatelessWidget {
  const LinkDialog({
    Key key,
    @required this.model,
    @required this.bridge,
  }) : super(key: key);

  final BridgeLinkViewModel model;
  final BridgeModel bridge;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return StatefulBuilder(builder: (_, setState) {
      return AlertDialog(
          title: Text('${bridge.name}'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(S.of(context).linkBridgeInstruction),
              SizedBox(height: kVerticalPaddingBig),
              Image.asset('assets/img_pushlink_bridge.webp',
                  width: 96, color: theme.iconTheme.color),
              if (model.connectionError.isNotEmpty)
                Padding(
                  padding: const EdgeInsets.only(top: kVerticalPaddingBig),
                  child: Text(model.connectionError,
                      style: theme.textTheme.subtitle1
                          .copyWith(color: theme.errorColor)),
                )
            ],
          ),
          actions: [
            FlatButton(
              onPressed: () {
                model.cancelDialog();
                setState(() {});
              },
              child: Text(S.of(context).dialogCancel),
            ),
            FlatButton(
              onPressed: () async {
                await model.linkBridge(bridge);
                setState(() {});
              },
              child: Text(S.of(context).dialogConnect),
            )
          ]);
    });
  }
}
